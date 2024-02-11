public class Circle
{
    private readonly CircleId id;
    private User owner;
    // private List<User> members;
    // Circle集約にmembersを直接持たせずに、idだけを持つようにする。不意にUserのメソッドを呼び出して変更してしまうことを防ぐ
    private List<UserId> memberIds;

    public Circle(CircleId id, CircleName name, User owner, List<UserId> memberIds)
    {
        if (id == null) throw new ArgumentNullException(nameof(id));
        if (name == null) throw new ArgumentNullException(nameof(name));
        if (owner == null) throw new ArgumentNullException(nameof(owner));
        if (memberIds == null) throw new ArgumentNullException(nameof(memberIds));

        Id = id;
        Name = name;
        Owner = owner;
        MemberIds = memberIds;
    }

    public bool isFull(){
        return memberIds.Count >= 30;
    }

    public int CountMembers(){
        return memberIds.Count + owner.Count;
    }

    public void Join(UserId memberId)
    {
        if (memberId == null) throw new ArgumentNullException(nameof(memberId));

        if (isFull())
        {
            throw new CircleFullException(id);
        }

        memberIds.Add(memberId);
    }

    public CircleId Id { get; }
    public CircleName Name { get; private set; }
    public List<UserId> MemberIds { get; private set; }
}
