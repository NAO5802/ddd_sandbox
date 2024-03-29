public class CircleService{
    private readonly ICircleRepository circleRepository;

    public CircleService(CircleRepository circleRepository){
        this.circleRepository = circleRepository;
    }

    public bool Exists(Circle circle){
        var duplicated = circleRepository.Find(circle.Name);
        return duplicated != null;
    }
}
