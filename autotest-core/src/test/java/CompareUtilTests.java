import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.openmonet.utils.CompareUtil;

/**
 * Проверка утили по сравнению значений
 */
public class CompareUtilTests {

    private final String ILLEGAL_ARG_EXEC_EXPECTED = "\nСравнение выполнено неверно! Ожидался IllegalArgumentException\n";
    private final String WRONG_COMPARING = "\nСравнение выполнено неверно!\n";

    @DataProvider
    public Object[][] positiveMore() {
        return new Object[][]{
                {"4", "2"},
                {"0", "-1"},
                {"0.6", "0.5"},
                {"-100", "-101"},
                {"20.000000", "19.999999"}
        };
    }

    @Test(groups = {"compare"}, dataProvider = "positiveMore", description = "позитивная проверка сравнения набора значений оператором \"больше\"")
    public void testPositiveMore(String var1, String var2) {
        Assert.assertTrue(CompareUtil.compare(var1, var2, "больше"), WRONG_COMPARING);
    }

    @Test(groups = {"compare"},dataProvider = "positiveMore", description = "позитивная проверка сравнения набора значений оператором \"меньше\"")
    public void testPositiveLess(String var1, String var2) {
        //используется провайдер от сравнения "больше", только меняем местами значения
        Assert.assertTrue(CompareUtil.compare(var2, var1, "меньше"), WRONG_COMPARING);
    }

    @DataProvider
    public Object[][] negativeNumeric() {
        return new Object[][]{
                {"+4", "2"},
                {"4f", "2"},
                {"4d", "2"},
                {"4q", "2"},
                {"q", "2"},
                {"", "2"},
                {"null", "2"},
                {"4", "2f"},
                {"4", "2d"},
                {"4", "2q"},
                {"4", "q"},
                {"4", ""},
                {"4", "null"},
                {"4", "4f"},
                {"4","4d"},
                {"4","4q"},
                {"4","+4"}
        };
    }

    @Test(groups = {"compare"},dataProvider = "negativeNumeric", description = "проверка выбрасывания исключения IllegalArgumentException для \"больше\"")
    public void testNegativeMore(String var1, String var2) {
        Boolean isMore = null;
        try {
            isMore = CompareUtil.compare(var1, var2, "больше");
        } catch (IllegalArgumentException exception) {
            //ожидаем этот exception
            //exception.printStackTrace();
        }
        Assert.assertNull(isMore, ILLEGAL_ARG_EXEC_EXPECTED);
    }

    @Test(groups = {"compare"},dataProvider = "negativeNumeric", description = "проверка выбрасывания исключения IllegalArgumentException для \"меньше\"")
    public void testNegativeLess(String var1, String var2) {
        Boolean isLess = null;
        try {
            isLess = CompareUtil.compare(var1, var2, "меньше");
        } catch (IllegalArgumentException exception) {
            //ожидаем этот exception
            //exception.printStackTrace();
        }
        Assert.assertNull(isLess, ILLEGAL_ARG_EXEC_EXPECTED);
    }

    @Test(groups = {"compare"},dataProvider = "negativeNumeric", description = "позитивная проверка оператором \"равно\" набором негативных данных для чисел")
    public void positiveEquals(String var1, String var2) {
        Assert.assertFalse(CompareUtil.compare(var1, var2, "равно"), WRONG_COMPARING);
    }

    @Test(groups = {"compare"},dataProvider = "negativeNumeric", description = "позитивная проверка оператором \"не равно\" набором негативных данных для чисел")
    public void positiveNotEquals(String var1, String var2) {
        Assert.assertTrue(CompareUtil.compare(var1, var2, "не равно"), WRONG_COMPARING);
    }

}