public interface ICircleRepository{
    void Save(Circle circle);
    Circle Find(CircleId id);
    Circle Find(CircleName name);

    List<Circle> FindRecommended(DateTime now);
}
