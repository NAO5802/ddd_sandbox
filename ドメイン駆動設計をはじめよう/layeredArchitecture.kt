class UserController : Controller {

    fun create(contactDetails: ContactDetails) {
        val result = _userService.create(contactDetails)
        return view(result)
    }
}

class UserService {
    fun create(contactDetails: ContactDetails) {
        var result: OperationResult = null;

        try {
            _db.startTransaction()

            val user = User()
            user.setContactDetails(contactDetails)
            user.save()

            _db.commit()
            result = OperationResult.Success
        } catch (ex: Exception) {
            _db.rollback()
            result = OperationResult.Exception(ex)
        }
    }

    return result
}
