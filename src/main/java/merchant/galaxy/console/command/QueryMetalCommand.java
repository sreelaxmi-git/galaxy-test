package merchant.galaxy.console.command;

import merchant.galaxy.application.model.GalacticSymbol;
import merchant.galaxy.application.model.Metal;
import merchant.galaxy.application.usecase.SymbolNotFoundException;
import merchant.galaxy.application.usecase.queryMetal.MetalNotFoundException;
import merchant.galaxy.application.usecase.queryMetal.QueryMetal;
import merchant.galaxy.application.usecase.queryMetal.QueryMetalRequest;
import merchant.galaxy.application.usecase.queryMetal.QueryMetalResponse;
import merchant.galaxy.console.display.DisplayMetal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryMetalCommand implements Command
{
  private final QueryMetal useCase;
  private final DisplayMetal display;

  private final Pattern pattern = Pattern.compile("how\\smany\\sCredits\\sis\\s([a-zA-Z\\s]+)\\s("+ Metal.eitherOneType()+")\\s\\?");

  public QueryMetalCommand(QueryMetal useCase, DisplayMetal display)
  {
    this.useCase = useCase;
    this.display = display;
  }

  @Override
  public boolean recognize(String commandPattern)
  {
    return pattern.matcher(commandPattern).matches();
  }

  @Override
  public void execute(String commandPattern)
  {
    QueryMetalRequest request = parse(commandPattern);
    try
    {
      QueryMetalResponse response = useCase.execute(request);
      display.show(response);
    }
    catch (SymbolNotFoundException symbol)
    {
      display.symbolNotFound();
    }
    catch (MetalNotFoundException metal)
    {
      display.metalNotFound();
    }
  }

  private QueryMetalRequest parse(String commandPattern)
  {
    Matcher matcher = pattern.matcher(commandPattern);
    matcher.matches();
    String symbol = matcher.group(1);
    String metal = matcher.group(2);
    return new QueryMetalRequest(
      Metal.valueOf(metal),
      new GalacticSymbol(symbol)
    );
  }
}
