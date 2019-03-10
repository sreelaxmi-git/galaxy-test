package merchant.galaxy.application.usecase.querySymbol;

import merchant.galaxy.application.model.GalacticSymbol;
import merchant.galaxy.application.service.GalacticToDecimalMapping;
import merchant.galaxy.application.usecase.SymbolNotFoundException;
import merchant.galaxy.application.usecase.UseCaseRequestReply;

public class QuerySymbol implements UseCaseRequestReply<GalacticSymbol,QuerySymbolResponse>
{

  private final GalacticToDecimalMapping mapping;

  public QuerySymbol(GalacticToDecimalMapping mapping)
  {
    this.mapping = mapping;
  }

  public QuerySymbolResponse execute(GalacticSymbol request)
  {
    Integer value = mapping.map(request).orElseThrow(SymbolNotFoundException::new);
    return new QuerySymbolResponse(request, value);
  }

}
