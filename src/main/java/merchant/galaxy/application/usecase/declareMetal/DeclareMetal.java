package merchant.galaxy.application.usecase.declareMetal;

import merchant.galaxy.application.model.Metal;
import merchant.galaxy.application.model.MetalValue;
import merchant.galaxy.application.repository.Repository;
import merchant.galaxy.application.service.GalacticToDecimalMapping;
import merchant.galaxy.application.usecase.SymbolNotFoundException;
import merchant.galaxy.application.usecase.UseCaseRequest;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DeclareMetal implements UseCaseRequest<MetalDeclaration>
{

  private final Repository<Metal, MetalValue> metalRepository;
  private final GalacticToDecimalMapping mapping;

  public DeclareMetal(Repository<Metal, MetalValue> metalRepository,
    GalacticToDecimalMapping mapping)
  {
    this.metalRepository = metalRepository;
    this.mapping = mapping;
  }

  @Override
  public void execute(MetalDeclaration declaration)
  {
    Integer numerosity = mapping.map(declaration.symbol)
      .orElseThrow(SymbolNotFoundException::new);
    BigDecimal singleValue = declaration.creditsValue.divide(BigDecimal.valueOf(numerosity));
    MetalValue metalValue = new MetalValue(declaration.metal,singleValue);
    metalRepository.add(metalValue);
  }

}
