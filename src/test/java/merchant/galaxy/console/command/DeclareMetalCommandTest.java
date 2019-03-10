package merchant.galaxy.console.command;

import merchant.galaxy.application.model.GalacticSymbol;
import merchant.galaxy.application.model.Metal;
import merchant.galaxy.application.usecase.declareMetal.DeclareMetal;
import merchant.galaxy.application.usecase.declareMetal.MetalDeclaration;
import merchant.galaxy.util.JUnitRuleClassMockery;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DeclareMetalCommandTest
{

  @Rule
  public JUnitRuleMockery context = new JUnitRuleClassMockery();

  @Mock
  private DeclareMetal useCase;

  private DeclareMetalCommand underTest;

  @Before
  public void setup()
  {
    underTest = new DeclareMetalCommand(useCase);
  }

  @Test
  public void unparseableCommand()
  {
    boolean recognize = underTest.recognize("this is not parsable");
    assertThat(recognize, is(false));
  }

  @Test
  public void parseableCommand()
  {
    boolean recognize = underTest.recognize("glob prok Gold is 57800 Credits");
    assertThat(recognize, is(true));
  }

  @Test
  public void succesfulExecution()
  {
    MetalDeclaration declaration = new MetalDeclaration(new GalacticSymbol(
      new String[]{"glob","prok"}),
      Metal.Gold,
      BigDecimal.valueOf(57800d)
    );
    context.checking(new Expectations()
    {{
      oneOf(useCase).execute(with(declaration));
    }});
    underTest.execute("glob prok Gold is 57800 Credits");
  }

  @Test
  public void succesfulExecution_withDecimalInput()
  {
    MetalDeclaration declaration = new MetalDeclaration(new GalacticSymbol(
      new String[]{"glob","prok"}),
      Metal.Gold,
      BigDecimal.valueOf(578.04d)
    );
    context.checking(new Expectations()
    {{
      oneOf(useCase).execute(with(declaration));
    }});
    underTest.execute("glob prok Gold is 578.04 Credits");
  }
}
