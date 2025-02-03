// ユースケース
// - 補完的な業務領域
// - 一般的な業務領域用の外部サービスとの連携
// - 区切られた文脈同士を連携するときのモデルの変換

class CreateUser {
    fun execute (userDetails: UserDetails) {
        try {
            _db.startTransaction()

            val user = User()
            user.name = userDetails.name
            user.email = userDetails.email
            user.save()

            _db.commit()
        } catch {
            _db.rollback()
            throw
        }
    }
}
