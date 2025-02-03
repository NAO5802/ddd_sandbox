// ユースケース
// - 補完的な業務領域
// - 一般的な業務領域用の外部サービスとの連携
// - 区切られた文脈同士を連携するときのモデルの変換

class LogVisit {
    fun execute(userId: Guid, visitedOn: Datetime) {
        try {
            _db.startTransaction()
            _db.execute("UPDATE users SET last_visit = @p1 WHERE user_id = @p2", visitedOn, userId)
            _db.execute("INSERT INTO VisitsLog(user_id, visited_on) VALUES(@p1, @p2)", userId, visitedOn)
            _db.commit()
        } catch {
            _db.rollback()
            throw
        }
    }
}


// 分散トランザクション
class LogVisit {
    fun execute(userId: Guid, visitedOn: Datetime) {
        _db.execute("UPDATE users SET last_visit = @p1 WHERE user_id = @p2", visitedOn, userId)
        // 失敗しうる
        _messageBus.publish("VISITS_TOPIC", User(userId: userId, visitDate: visitedOn))
    }
}

class LogVisit {
    fun execute(userId: Guid, expectedVisit: Long) {
        _db.execute("UPDATE users SET visits = visits+1 WHERE user_id = @p1 AND visits = @p2", userId, expectedVisit)
    }
}
