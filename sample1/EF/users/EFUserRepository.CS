public class EFUserRepository : IUserRepository
{
    public void Save(User user)
    {
        // var userDataModel = new UserDataModel
        // {
        //     Id = user.Id.Value,
        //     Name = user.Name.Value
        // };

        // 通知オブジェクトを引き渡して内部データを取得
        var userDataModelBuilder = new UserDataModelBuilder();
        user.Notify(userDataModelBuilder);

        var userDataModel = userDataModelBuilder.Build();

        context.Users.Add(userDataModel);
        context.SaveChanges();

    }
}
