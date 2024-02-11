public class CircleApplicationService
{
    private readonly ICircleFactory circleFactory;
    private readonly ICircleRepository circleRepository;
    private readonly CircleService circleService;
    private readonly IUserRepository userRepository;

    private readonly DateTime now;

    public CircleApplicationService(
        ICircleFactory circleFactory,
        ICircleRepository circleRepository,
        CircleService circleService,
        IUserRepository userRepository
    )
    {
        this.circleFactory = circleFactory;
        this.circleRepository = circleRepository;
        this.circleService = circleService;
        this.userRepository = userRepository;
    }

    public void Create(CircleCreateCommand command)
    {
        using (var transaction = new TransactionScope())
        {
            var ownerId = new UserId(command.UserId);
            var owner = userRepository.Find(ownerId);
            if (owner == null)
            {
                throw new UserNotFoundException("サークルのオーナーとなるユーザが見つかりませんでした");
            }

            var name = new CircleName(command.Name);
            var circle = circleFactory.Create(name, owner);
            if (circleService.Exists(circle))
            {
                throw new CanNotRegisterCircleException(circle, "サークルは既に存在しています");
            }

            circleRepository.Save(circle);

            transaction.Complete();
        }
    }

    public void Join(CircleJoinCommand command)
    {
        using (var transaction = new TransactionScope())
        {
            var memberId = new UserId(command.UserId);
            var member = userRepository.Find(memberId);
            if (member == null)
            {
                throw new UserNotFoundException(memberId, "ユーザが見つかりませんでした");
            }

            var circleId = new CircleId(command.CircleId);
            var circle = circleRepository.Find(circleId);
            if (circle == null)
            {
                throw new CircleNotFoundException(circleId, "サークルが見つかりませんでした");
            }

            // FCCに詰め替えるのをアプリケーションサービスで行う(直接userRepositoryをもたせない)
            var owner = userRepository.Find(circle.Owner.Id);
            var members = userRepository.Find(circle.MemberIds);
            var circleMembers = circleMembers(circleId, owner, members);

            var circleFullSpecification = new CircleFullSpecification();
            if (circleFullSpecification.IsSatisfiedBy(circleMembers))
            {
                throw new CircleFullException(circleId, "サークルが一杯です");
            }

            circle.Join(member);
            circleRepository.Save(circle);

            transaction.Complete();
        }
    }

    public CircleGetRecommendResult GetRecommend(CircleGetRecommendRequest request){
        var recommendCircleSpec = new CircleRecommendSpecification(now);

        var circles = circleRepository.FindAll();
        var recommendCircles = circles.Where(recommendCircleSpec.IsSatisfiedBy).Take(10).ToList();

        return new CircleGetRecommendResult(recommendCircles);
    }
}
