// First class collection
public class CircleMembers{
    private readonly User owner;
    private readonly List<User> members;

    public CircleMembers(CircleId id, User owner, List<User> members){
        Id = id;
        Owner = owner;
        Members = members;
    }

    public CircleId Id { get; }

    public int CountMembers(){
        return members.Count + owner.Count;
    }

    public int CountPremiumMembers(bool containsOwner = true){
        var premiumUserNumber = members.Count(user => user.IsPremium);

        if(containsOwner){
            return premiumUserNumber + (owner.IsPremium ? 1 : 0);
        } else {
            return premiumUserNumber;
        }
    }
}
