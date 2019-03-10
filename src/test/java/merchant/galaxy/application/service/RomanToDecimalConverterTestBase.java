package merchant.galaxy.application.service;

import merchant.galaxy.application.model.RomanNumber;
import org.junit.Before;

public class RomanToDecimalConverterTestBase
{

  protected RomanToDecimalConverter romanToDecimalConverter;

  @Before
  public void setUp()
  {
    romanToDecimalConverter = new RomanToDecimalConverter();
  }
}
