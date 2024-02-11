public class User{
    // インスタンス変数は非公開にする
    private readonly UserId id;
    private UserName name;

    public void Notify(IUserNotification notification){
        // 内部データを通知
        notification.Id(id);
        notification.Name(name);
    }
}
