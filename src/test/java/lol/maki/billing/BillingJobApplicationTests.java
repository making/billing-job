package lol.maki.billing;

import lol.maki.billing.model.Bill;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class BillingJobApplicationTests {
    private final JdbcTemplate jdbcTemplate;

    public BillingJobApplicationTests(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Test
    void testJobResults() {
        List<Bill> billStatements = this.jdbcTemplate.query("select id, first_name, last_name, minutes, data_usage, bill_amount FROM bill_statements ORDER BY id",
                (rs, rowNum) -> new Bill(rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getLong("data_usage"),
                        rs.getLong("minutes"),
                        rs.getBigDecimal("bill_amount")));
        assertThat(billStatements.size()).isEqualTo(5);
        Bill billStatement = billStatements.get(0);
        assertThat(billStatement.getBillAmount()).isEqualTo(new BigDecimal("6.00"));
        assertThat(billStatement.getFirstName()).isEqualTo("jane");
        assertThat(billStatement.getLastName()).isEqualTo("doe");
        assertThat(billStatement.getId()).isEqualTo(1);
        assertThat(billStatement.getMinutes()).isEqualTo(500);
        assertThat(billStatement.getDataUsage()).isEqualTo(1000);
    }
}
