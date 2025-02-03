// 中核の業務領域（業務ロジックが複雑）の実装に選択する
data class Color(
    val red: Byte,
    val green: Byte,
    val blue: Byte
) {
    fun mixWith(other: Color): Color {
        return Color(
            java.lang.Math.min(red + other.red, 255),
            java.lang.Math.min(green + other.green, 255),
            java.lang.Math.min(blue + other.blue, 255),
        )
    }
}

class Ticket {
    fun addMessage(from: UserId, body: String) {
        val message = Message(from, body)
        _messages.append(message)
    }

    fun execute(cmd: AddMessage) {
        val message = Message(cmd.from, cmd.body)
        _messages.append(message)
    }
}

fun escalate(id: TicketId, reason: EscalationReason): ExecutionResult {
    try {
        val ticket = _ticketRepository.load(id)
        val cmd = Escalate(reason)
        ticket.execute(cmd)
        _ticketRepository.save(ticket)
        return ExecutionResult.Success
    } catch (ex: ConcurrencyException) {
        return ExceptionResult.Error(ex)
    }
}

// 複数エンティティが集まった集約を使って業務ルールを表現する
class Ticket(
    private var messages: List<Message>,
    val customerId: CustomerId,
    val assignedAgentId: AgentId,
    val productIds: List<ProductId>
) {
    fun execute(cmd: EvaluateAutomaticActions) {
        if (isEscalated &&
            remainingTimePercentage < 0.5 &&
            getUnreadMessageCount(assignedAgentId) > 0
        ) {
            agent = assignNewAgent()
        }
    }

    // Messageエンティティの状態を変更する。アクセスできるのは集約ルート（つまりTicket）からのみ
    fun execute(cmd: AcknowledgeMessage) {
        val message = messages.find { it.id == cmd.id }
        message.wasRead = true
    }

    private fun getUnreadMessageCount(id: AgentId): Int {
        return messages.filter { messages => message.to == id && !message.wasRead }.count()
    }
}

// 業務イベント
class Ticket(private var domeinEvents: MutableList<DomainEvent>) {
    fun execute(cmd: RequestEscalation) {
        if(!isEscalated && RemainingTimePercentage <= 0){
            isEscalated = true
            val escalatedEvent = TicketEscalated(id, cmd.reason)
            domeinEvents.add(escalatedEvent)
        }
    }
}

// 業務サービス。自分自身の状態は持たないステートレスなオブジェクト
class ResponseTimeFrameCaluculationService {
    fun calculateAgentResponseDeadline(agentId: AgentId, priority: Priority, escalated: Boolean, startTime: DateTime){
        val policy = _departmentRepository.getDepartmentPolicy(agentId)
        var MaxProcTime = policy.getMaxResponseTimeFor(priority)
        if(isEscalated){
            maxProcTime = maxProcTime * policy.escalationFactor
        }

        val shifts = _departmentRepository.getUpcomingShifts(agentId, startTime, startTime.plusHours(policy.maxAgentResponseTime))

        return caluculateTargetTime(maxProcTime, shifts)
    }
}
