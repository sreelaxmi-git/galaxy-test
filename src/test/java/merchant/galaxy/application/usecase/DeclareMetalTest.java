package merchant.galaxy.application.usecase;

import merchant.galaxy.application.model.GalacticSymbol;
import merchant.galaxy.application.model.Metal;
import merchant.galaxy.application.model.MetalValue;
import merchant.galaxy.application.repository.Repository;
import merchant.galaxy.application.service.GalacticToDecimalMapping;
import merchant.galaxy.application.service.RomanToDecimalConverter;
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
import java.util.Optional;

public class DeclareMetalTest
{
  private DeclareMetal underTest;

  @Rule
  public JUnitRuleMockery mockery = new JUnitRuleClassMockery();

  @Mock
  private Repository<Metal,MetalValue> repositoryMetal;
  @Mock
  private GalacticToDecimalMapping mapping;
  @Mock
  private RomanToDecimalConverter converter;

  @Before
  public void setup()
  {
    underTest = new DeclareMetal(repositoryMetal,
      mapping);
  }

  @Test(expected = SymbolNotFoundException.class)
  public void whenASymbolIsMissing_throwException()
  {
    GalacticSymbol symbol = new GalacticSymbol(new String[] {"missing"});
    MetalDeclaration request = new MetalDeclaration(symbol
      , Metal.Silver, BigDecimal.valueOf(34)
    );

    mockery.checking(new Expectations()
    {{
      oneOf(mapping).map(symbol);
      will(returnValue(Optional.empty()));
    }});
    underTest.execute(request);
  }

  @Test
  public void whenADeclarationIsPerformed_aGalacticSymbolIsNeeded()
  {
    GalacticSymbol symbol = new GalacticSymbol(new String[] {"glob", "glob"});
    MetalDeclaration request = new MetalDeclaration(symbol
      , Metal.Silver,BigDecimal.valueOf(34)
    );
    MetalValue metalValue = new MetalValue(
      Metal.Silver,BigDecimal.valueOf(34).divide(BigDecimal.valueOf(2))
    );

    mockery.checking(new Expectations()
    {{
      oneOf(mapping).map(symbol);
      will(returnValue(Optional.of(2)));
      allowing(repositoryMetal).add(with(metalValue));
    }});
    underTest.execute(request);
  }

  @Test
  public void whenADeclarationIsPerformed_metalIsAddedToTheRepo()
  {
    GalacticSymbol symbol = new GalacticSymbol(new String[] {"glob", "glob"});
    MetalDeclaration request = new MetalDeclaration(symbol
      ,Metal.Silver,BigDecimal.valueOf(34)
    );
    MetalValue metalValue = new MetalValue(
      Metal.Silver,BigDecimal.valueOf(34).divide(BigDecimal.valueOf(2))
    );

    mockery.checking(new Expectations()
    {{
      oneOf(mapping).map(symbol);
      will(returnValue(Optional.of(2)));
      oneOf(repositoryMetal).add(with(metalValue));
    }});
    underTest.execute(request);
  }

}
