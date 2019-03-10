package merchant.galaxy.console;

import merchant.galaxy.application.model.Metal;
import merchant.galaxy.application.model.MetalValue;
import merchant.galaxy.application.model.SymbolMapping;
import merchant.galaxy.inmemory.InMemoryMetalValueRepository;
import merchant.galaxy.inmemory.InMemorySymbolMappingRepository;
import merchant.galaxy.application.repository.Repository;
import merchant.galaxy.application.service.GalacticToDecimalMapping;
import merchant.galaxy.application.service.RomanToDecimalConverter;
import merchant.galaxy.application.usecase.declareMetal.DeclareMetal;
import merchant.galaxy.application.usecase.declareSymbol.DeclareSymbol;
import merchant.galaxy.application.usecase.queryMetal.QueryMetal;
import merchant.galaxy.application.usecase.querySymbol.QuerySymbol;
import merchant.galaxy.console.command.DeclareMetalCommand;
import merchant.galaxy.console.command.DeclareSymbolCommand;
import merchant.galaxy.console.command.QueryMetalCommand;
import merchant.galaxy.console.command.QuerySymbolCommand;
import merchant.galaxy.console.display.DisplayMetal;
import merchant.galaxy.console.display.DisplaySymbol;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

public class Main
{
  public static void main(String[] args)
  {

    Repository<String,SymbolMapping> symbolRepo = new InMemorySymbolMappingRepository(
      new HashMap<>()
    );
    Repository<Metal, MetalValue> metalRepo = new InMemoryMetalValueRepository(
      new HashMap<>()
    );
    RomanToDecimalConverter converter = new RomanToDecimalConverter();
    GalacticToDecimalMapping galacticToDecimalMapping = new GalacticToDecimalMapping(symbolRepo, converter);

    Reader reader;
    try
    {
      if(args.length==1)
      {
        reader = new FileReader(args[0]);
      } else
      {
        System.out.println("Welcome to Merchant's Guide to the Galaxy!");
        System.out.println(".. in order to exit from this program you should send a SIGINT (with Ctrl-C for example).");
        reader = new InputStreamReader(System.in);
      }

      new ProgramLoop(
        new DeclareSymbolCommand(
          new DeclareSymbol(symbolRepo)
        ),
        new DeclareMetalCommand(
          new DeclareMetal(metalRepo, galacticToDecimalMapping)
        ),
        new QuerySymbolCommand(
          new QuerySymbol(galacticToDecimalMapping),
          new DisplaySymbol(System.out)
        ),
        new QueryMetalCommand(
          new QueryMetal(metalRepo, galacticToDecimalMapping),
          new DisplayMetal(System.out)
        )
      ).run(reader,System.out);
    }
    catch (FileNotFoundException e)
    {
      System.out.println(e.getMessage());
    }
  }
}
