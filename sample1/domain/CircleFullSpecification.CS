public class CircleFullSpecification{

    public bool IsSatisfiedBy(CircleMembers members){
        var premiumUserNumber = members.CountPremiumMembers(false);
        var circleUpperLimit = premiumUserNumber < 10 ? 30 : 50;

        return members.CountMembers() >= circleUpperLimit;
    }
}
