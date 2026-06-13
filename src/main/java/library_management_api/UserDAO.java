package library_management_api;

import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO {

    private final JdbcTemplate jdbcTemplate;

    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> findActiveUserForLogin(String studentNo, String password) {
        String sql = """
                SELECT user_id, student_no, name, role_level, status
                FROM users
                WHERE student_no = ?
                  AND password = ?
                  AND status = 'ACTIVE'
                """;

        try {
            User user = jdbcTemplate.queryForObject(sql, (resultSet, rowNumber) -> new User(
                    resultSet.getInt("user_id"),
                    resultSet.getString("student_no"),
                    resultSet.getString("name"),
                    resultSet.getString("role_level"),
                    resultSet.getString("status")
            ), studentNo, password);

            return Optional.of(user);
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    public boolean studentNoExists(String studentNo) {
        String sql = "SELECT COUNT(*) FROM users WHERE student_no = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, studentNo);
        return count != null && count > 0;
    }

    public void registerStudent(RegisterRequest request) {
        String sql = """
                INSERT INTO users (student_no, name, password, role_level, created_at, status)
                VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, 'ACTIVE')
                """;

        jdbcTemplate.update(sql,
                request.getStudentNo(),
                request.getName(),
                request.getPassword(),
                request.getRoleLevel());
    }
}
