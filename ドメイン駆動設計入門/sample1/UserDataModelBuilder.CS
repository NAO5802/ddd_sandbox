public class UserDataModelBuilder: IUserNotification{
    // 通知されたデータをインスタンス変数で保持する
    private UserId id;
    private UserName name;

    public void Id(UserId id){
        this.id = id;
    }

    public void Name(UserName name){
        this.name = name;
    }

    public UserDataModelBuilder Build(){
        return new UserDataModelBuilder{
            id = id.Value,
            name = name.Value
        }
    }
}
