public class Circle
{
    private readonly CircleId id;
    private User owner;
    // private List<User> members;
    // Circle集約にmembersを直接持たせずに、idだけを持つようにする。不意にUserのメソッドを呼び出して変更してしまうことを防ぐ
    private List<UserId> memberIds;

    public Circle(CircleId id, CircleName name, User owner, List<User> members)
    {
        if (id == null) throw new ArgumentNullException(nameof(id));
        if (name == null) throw new ArgumentNullException(nameof(name));
        if (owner == null) throw new ArgumentNullException(nameof(owner));
        if (members == null) throw new ArgumentNullException(nameof(members));

        Id = id;
        Name = name;
        Owner = owner;
        Members = members;
    }

    public bool isFull(){
        // サークルのオーナーを含めて30名か確認
        return members.Count >= 29;
    }

    public void Join(User member)
    {
        if (member == null) throw new ArgumentNullException(nameof(member));

        if (isFull())
        {
            throw new CircleFullException(id);
        }

        members.Add(member);
    }

    public CircleId Id { get; }
    public CircleName Name { get; private set; }
    public List<UserId> MemberIds { get; private set; }
}
