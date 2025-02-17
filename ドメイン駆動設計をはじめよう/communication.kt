import java.awt.DisplayMode
import java.lang.Exception
import java.net.NoRouteToHostException
import javax.print.attribute.standard.Destination

// 集約同士の連携

// 以下NG例の解決策のひとつとして、送信箱(Outbox)があるので参照する(9.2.1)
// NG例1
class Campaign {
    private var events: MutableList<DomainEvent>
    private var messageBus: IMessageBus
//    ...

    fun deactivate(reason: String): Unit {
        for (location in locations.values()) {
            location.deactivate()
        }

        isActive = false

        val newEvent = CamopaingDeactivated(id, reason)
        events.add(newEvent)
        // 1. DBにコミットする前に業務イベントを外部に発信しているのがよくない。購読側が通知を受け取った時点で不整合になっている可能性がある
        // 2. DBへのコミットが失敗した場合、DBはロールバックされるが購読側には通知が届いたままになり、この業務イベントは撤回できない。
        messageBus.publish(newEvent)
    }
}

// NG例2
class ManagementApi {
    private val messageBus: IMessageBus
    private val repository: ICampaignRepository
    // ...

    fun deactiveCampaign(id: CampaignId, reason: String): ExecutionResult {
        try {
            val campaign = repository.load(id)
            campaign.deactivate(reason)
            repository.commitChnages(campaign)

            // DBコミット成功後に業務イベントを発行しているが、この業務イベント発行は失敗しうる
            val events = campaign.getUnpublishedEvents()
            for (event: IDomainEvent in events) {
                messageBus.publish(event)
            }

            campaign.clearUnpublishedEvents()


        } catch (ex: Exception) {
            // ...
        }
    }
}

// サーガ
class CampaignPublishingSaga {
    private val repository: ICampaignRepository
    private val publishingService: IPublishingService

    fun process(event: CampaignActived): Unit {
        val campaign = repository.load(event.campaignId)
        val advertisingMaterials = campaigin.generateAdvertisingMaterials()
        publishingService.submitAdvertisement(event.campaignId, advertisingMaterials)
    }

    fun process(event: PublishingConfirmed): Unit {
        val campaign = repository.load(event.campaignId)
        campaign.trackPublishingConfirmation(event.confirmarionId)
        repository.commitChanges(campaign)
    }

    fun process(event: PublishingRejected): Unit {
        val campaign = repository.load(event.campaignId)
        campaign.trackPublisingRejection(event.rejectionReason)
        repository.commitChanges(campaign)
    }
}

// プロセスマネージャー
class BookingProcessManager {
    private var events: IList<IDomainEvent>
    private val id: BookingId
    private var distination: Distination
    private var paramaters: TripDefinition
    private var traveler: EmploueeId
    private var route: Route
    private var rejectedRoutes: IList<Route>
    private val routing: IRoutingService

    fun initialize(destination: Destination, parameters: TripDefinition, traveler: EmployeeId): Unit {
        distination = destination
        paramaters = parameters
        traveler = traveler
        route = routing.calculate(destination, parameters)

        val routeGenerated = RouteGeneratedEvent(bookingId: id, route: route)
        val commandIssuedEvent = CommandIssuedEvent(command: RequestEmployeeApproval(traveler, route))

        events.add(routeGene rated)
        events.add(commandIssuedEvent)
    }

    fun process(event: routeConfirmed): Unit{
        val commandIssuedEvent = CommandIssuedEvent(command: BookFlights(route, paramaters))

        events.add(event)
        events.add(commandIssuedEvent)
    }

    fun process(event: RouteRejected): Unit{
        val commandIssuedEvent = CommandIssuedEvent(command: RequestRerouting(traveler, route))

        events.add(event)
        events.add(commandIssuedEvent)
    }

    fun process(event: ReroutingConfirmed): Unit{
        rejectedRoutes.add(route)
        route = routing.calculateAltRoute(destination, parameters, rejectedRoutes)
        val routeGenerated = RouteGeneratedEvent(bookingId: id, route: route)
        val commandIssuedEvent = CommandIssuedEvent(command: RequestEmployeeApproval(traveler, route))

        events.add(event)
        events.add(routeGenerated)
        events.add(commandIssuedEvent)
    }

    fun process(event: FlightBooked): Unit{
        val commandIssuedEvent = CommandIsseudEvent(command: BookHotel(destination, parameters))

        events.add(event)
        events.add(commandIssuedEvent)
    }
}
