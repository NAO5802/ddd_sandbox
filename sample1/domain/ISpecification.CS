public interface ISpecification<T>{
    public bool IsSatisfiedBy(T value);
}
