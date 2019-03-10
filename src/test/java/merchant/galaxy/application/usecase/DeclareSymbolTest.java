package merchant.galaxy.application.usecase;

import merchant.galaxy.application.model.RomanDigit;
import merchant.galaxy.application.model.SymbolMapping;
import merchant.galaxy.application.repository.Repository;
import merchant.galaxy.application.usecase.declareSymbol.DeclareSymbol;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class DeclareSymbolTest
{
  private DeclareSymbol underTest;

  @Rule
  public JUnitRuleMockery mockery = new JUnitRuleMockery();

  @Mock
  private Repository<String,SymbolMapping> repository;

  @Before
  public void setup()
  {
    underTest = new DeclareSymbol(repository);
  }

  @Test
  public void whenADeclarationIsPerformed_symbolIsAddedToTheRepo()
  {
    SymbolMapping request = new SymbolMapping("word", RomanDigit.D);

    mockery.checking(new Expectations()
    {{
      oneOf(repository).add(with(request));
    }});
    underTest.execute(request);
  }

}
