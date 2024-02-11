public class UserGetInteractor : IUserInputPort
{
    private readonly IUserRepository userRepository;
    private readonly IUserGetPresenter presenter

    public UserGetInteractor(IUserRepository userRepository, IUserGetPresenter presenter)
    {
        this.userRepository = userRepository;
        this.presenter = presenter;
    }

    public void Handle(UserGetInputData inputData)
    {
        var targetId = new UserId(inputData.UserId);
        var user = userRepository.Find(targetId);

        var userData = new UserData(user.Id.Value, user.Name.Value);
        var outputData = new UserUpdateOutputData(userData);

        presenter.Output(outputData);
    }
}
