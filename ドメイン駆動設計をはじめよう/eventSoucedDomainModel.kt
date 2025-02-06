import java.util.HashSet

// 中核の業務領域（業務ロジックが複雑）の実装に選択する
class LeadStateModelProjection {
    private var leadId: Long
    private var firstName: String
    private var lastName: String
    private var status: LeadStatus
    private var phoneNumber: PhoneNumber
    private var followupOn: DateTime?
    private var createdOn: DateTime
    private var updatedOn: DateTime
    private var version: Int

    fun apply(event: LeadInitialized) {
        leadId = event.leadId
        status = LeadStatus.NEW_LEAD
        firstName = event.firstName
        lastName = event.lastName
        phoneNumber = event.phoneNumber
        createdOn = event.timestamp
        updatedOn = event.timestamp
        version = 0
    }

    fun apply(event: Contacted) {
        updatedOn = event.timestamp
        followupOn = event.followupOn
        status = LeadStatus.FOLLOWUP_SET
        version += 1
    }

    fun apply(event: ContactDetailChanged) {
        firstName = event.firstName
        lastName = event.lastName
        phoneNumber = event.phoneNumber
        updatedOn = event.timestamp
        version += 1
    }

    fun apply(event: OrderSubmitted) {
        updatedOn = event.timestamp
        status = LeadStatus.PENDING_PAYMENT
        version += 1
    }

    fun apply(event: PaymentConfirmed) {
        updatedOn = event.timestamp
        status = LeadStatus.CONVERTED
        version += 1
    }
}

class LeadSearchModelProjection {
    private var leadId: Long
    private var firstNames: HashSet<String>
    private var lastNames: HashSet<String>
    private var phoneNumbers: HashSet<PhoneNumber>
    private var version: Int

    fun apply(event: LeadInitialized) {
        leadId = event.leadId
        version = 0

        firstNames = HashSet<String>()
        lastNames = HashSet<String>()
        phoneNumbers = HashSet<PhoneNumber>()
        firstNames.add(event.firstName)
        lastNames.add(event.lastName)
        phoneNumbers.add(event.phoneNumber)
    }

    fun apply(event: ContactDetailChanged) {
        firstNames.add(event.firstName)
        lastNames.add(event.lastName)
        phoneNumbers.add(event.phoneNumber)
        version += 1
    }

    fun apply(event: Contacted) {
        version += 1
    }

    fun apply(event: FollowupSet) {
        version += 1
    }

    fun apply(event: OrderSubmitted) {
        version += 1
    }

    fun apply(event: PaymentConfirmed) {
        version += 1
    }
}

class AnalysisModelProjection {
    private var leadId: Long
    private var followups: Int
    private var status: LeadStatus
    private var version: Int

    fun apply(event: LeadInitialized) {
        leadId = event.leadId
        followups = 0
        status = LeadStatus.NEW_LEAD
        version = 0
    }

    fun apply(event: Contacted) {
        version += 1
    }

    fun apply(event: followupSet) {
        status = LeadStatus.FOLLOWUP_SET
        // 商談を予定した回数(followups)を数えるようにした
        followups += 1
        version += 1
    }

    fun apply(event: ContactDetailChanged) {
        version += 1
    }

    fun apply(event: Ordersubmitted) {
        status = LeadStatus.PENDING_PAYMENT
        version += 1
    }

    fun apply(event: PaymentConfirmed) {
        status = LeadStatus.CONVERTED
        version += 1
    }
}

// イベントを永続化する
interface IEventStore {
    fun fetch(instanceId: Guid): IEnumerable<Event>
    fun append(instanceid: Guid, newEvents: List<Event>, expectedVersion: Int): Unit
}

// イベント履歴式集約
class TicketAPI(
    private val ticketsRepository: ITicketRepository
) {
    fun requestEscalation(id: TicketId, reason: EscalationReason) {
        val events = ticketsRepository.loadEvents(id)
        val ticket = Ticket(events)
        val originalVersion = ticket.version
        val cmd = RequestEscalation(reason)
        ticket.execute(cmd)
        ticketsRepository.commitChanges(ticket, originalVersion)
    }

}

class Ticket {
    private var domainEvents = mutableListOf<DomainEvent>()
    private var state TicketState

    constructor(events: IEnumerable<IDomainEvents>) {
        state = TicketState()
        events.forEach{
            appendEvent(it)
        }
    }

    private fun appendEvent(event: IDomainEvent): Unit{
        domainEvents.add(event)
        state.apply(event)
    }

    fun execute(cmd: RequestEscalation): Unit{
        // 通常の集約ではTicketオブジェクトのフラグをtrueにした。
        // イベント履歴式集約ではescalatedEventを生成して、appendEventに渡す
        if(!state.isEscalated && state.remaingTimePercentage <= 0){
            val escalatedEvent = TicketEscalated(id, cmd.reason)
            appendEvent(escalatedEvent)
        }
    }
}

class TicketState{
    private var id: TicketId
    private var version: Int
    private var isEscalated: Boolean

    fun apply(event: TicketInitialized): Unit{
        id = event.id
        version = 0
        isEscalated = false
    }

    fun apply(event: TicketEscalated): Unit{
        isEscalated = true
        version += 1
    }
}
