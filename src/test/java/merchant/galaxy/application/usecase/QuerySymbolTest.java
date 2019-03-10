package merchant.galaxy.application.usecase;

import merchant.galaxy.application.model.GalacticSymbol;
import merchant.galaxy.application.model.Metal;
import merchant.galaxy.application.model.MetalValue;
import merchant.galaxy.application.service.GalacticToDecimalMapping;
import merchant.galaxy.application.usecase.queryMetal.QueryMetalRequest;
import merchant.galaxy.application.usecase.querySymbol.QuerySymbol;
import merchant.galaxy.application.usecase.querySymbol.QuerySymbolResponse;
import merchant.galaxy.util.JUnitRuleClassMockery;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuerySymbolTest
{

  @Rule
  public JUnitRuleMockery mockery = new JUnitRuleClassMockery();

  @Mock
  private GalacticToDecimalMapping mapping;

  private QuerySymbol underTest;

  @Before
  public void setup()
  {
    underTest = new QuerySymbol(mapping);
  }

  @Test
  public void useGalacticToDecimalMapping()
  {
    String[] list = new String[]{"firstSymbol","secondSymbol","thirdSymbol"};
    GalacticSymbol request = new GalacticSymbol(list);

    mockery.checking(new Expectations()
    {{
      oneOf(mapping).map(request);
      will(returnValue(Optional.of(10)));
    }});

    underTest.execute(request);
  }

  @Test
  public void returnUseCaseResponse()
  {
    String[] list = new String[]{"firstSymbol","secondSymbol","thirdSymbol"};
    GalacticSymbol request = new GalacticSymbol(list);

    mockery.checking(new Expectations()
    {{
      oneOf(mapping).map(request);
      will(returnValue(Optional.of(10)));
    }});

    QuerySymbolResponse response = underTest.execute(request);
    assertThat(response.symbol,is(request));
    assertThat(response.value,is(10));
  }

  @Test(expected = SymbolNotFoundException.class)
  public void whenASymbolIsMissing_throwException()
  {
    String[] list = new String[]{"firstSymbol","secondSymbol","thirdSymbol"};
    GalacticSymbol request = new GalacticSymbol(list);

    mockery.checking(new Expectations()
    {{
      oneOf(mapping).map(request);
      will(returnValue(Optional.empty()));
    }});

    underTest.execute(request);
  }


}
