public class CircleQueryService()
{

    public CircleGetSummariesResult GetSummaries(CircleGetSummariesCommand command)
    {
        var connection = provider.Connection;

        using (var sqlCommand = connection.CreateCommand())
        {
            sqlCommand.CommandText = @"
                SELECT
                    circles.id as circleId,
                    users.name as ownerName
                FROM circles
                LEFT OUTER JOIN users
                ON circles.owner_id = users.id
                ORDER BY circles.id
                OFFSET @skip ROWS
                FETCH NEXT @size ROWS ONLY
                ";

            var page = command.Page;
            var size = command.Size;
            sqlCommand.Parameters.Add(new SqlParameter("@skip", (page - 1) * size));
            sqlCommand.Parameters.Add(new SqlParameter("@size", size));

            using (var reader = sqlCommand.ExecuteReader())
            {
                var summaries = new List<CircleSummaryData>();
                while (reader.Read())
                {
                    var circleId = (string)reader["circleId"];
                    var ownerName = (string)reader["ownerName"];
                    var summary = new CircleSummaryData(circleId, ownerName);
                    summaries.Add(summary);
                }

                return new CircleGetSummariesResult(summaries);
            }
        }
    }
}
