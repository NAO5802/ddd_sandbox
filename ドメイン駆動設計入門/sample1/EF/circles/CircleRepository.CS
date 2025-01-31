public class CircleRepository: ICircleRepository{
    private readonly SqlConnection connection;

// ...略

    public List<Circle> FindRecommended(ISpecification<Circle> specification){
        using(var command = connection.CreateCommand)
        {
            // 全権取得するクエリを発行
            command.CommandText = "SELECT * FROM circles";

            using(var reader = command.ExecuteReader())
            {
                var circles = new List<Circle>();
                while(reader.Read())
                {
                    // インスタンスを生成して、条件に合うか確認している（合わなければ捨てられる）
                    // 仕様をリポジトリのフィルターとして扱う時は、パフォーマンスのことを考慮する必要がある
                    var circle = CreateInstance(reader);
                    if(specification.IsSatisfiedBy(circle))
                    {
                        circles.Add(circle);
                    }
                }

                return circles;
            }
        }

    }

}
