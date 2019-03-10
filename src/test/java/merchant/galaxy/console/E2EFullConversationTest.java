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
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class E2EFullConversationTest
{

  private ProgramLoop programLoop;
  private ByteArrayOutputStream baos;
  private PrintStream printStream;

  @Before
  public void setup()
  {
    Repository<String,SymbolMapping> symbolRepo = new InMemorySymbolMappingRepository(
      new HashMap<>()
    );
    Repository<Metal, MetalValue> metalRepo = new InMemoryMetalValueRepository(
      new HashMap<>()
    );
    RomanToDecimalConverter converter = new RomanToDecimalConverter();
    GalacticToDecimalMapping galacticToDecimalMapping = new GalacticToDecimalMapping(symbolRepo, converter);

    baos = new ByteArrayOutputStream();
    printStream = new PrintStream(baos);

    programLoop = new ProgramLoop(
      new DeclareSymbolCommand(
        new DeclareSymbol(symbolRepo)
      ),
      new DeclareMetalCommand(
        new DeclareMetal(metalRepo, galacticToDecimalMapping)
      ),
      new QuerySymbolCommand(
        new QuerySymbol(galacticToDecimalMapping),
        new DisplaySymbol(printStream)
      ),
      new QueryMetalCommand(
        new QueryMetal(metalRepo, galacticToDecimalMapping),
        new DisplayMetal(printStream)
      )
    );
  }

  @Test
  public void fullTextWithUnparseableLine()
  {
    String input = "glob is I\n" +
      "prok is V\n" +
      "pish is X\n" +
      "tegj is L\n" +
      "glob glob Silver is 34 Credits\n" +
      "glob prok Gold is 57800 Credits\n" +
      "pish pish Iron is 3910 Credits\n" +
      "how much is pish tegj glob glob ?\n" +
      "how many Credits is glob prok Silver ?\n" +
      "how many Credits is glob prok Gold ?\n" +
      "how many Credits is glob prok Iron ?\n" +
      "how much wood could a woodchuck chuck if a woodchuck could chuck wood ?\n";
    String expected = "pish tegj glob glob is 42\n" +
      "glob prok Silver is 68 Credits\n" +
      "glob prok Gold is 57800 Credits\n" +
      "glob prok Iron is 782 Credits\n" +
      "I have no idea what you are talking about\n";

    programLoop.run(new StringReader(input), printStream);
    String output = new String(baos.toByteArray(), StandardCharsets.UTF_8);
    assertThat(output,is(equalTo(expected)));
  }

  @Test
  public void fullTextWithSymbolNotFoundInSymbolQuery()
  {
    String input = "glob is I\n" +
      "prok is V\n" +
      "pish is X\n" +
      "tegj is L\n" +
      "glob glob Silver is 34 Credits\n" +
      "glob prok Gold is 57800 Credits\n" +
      "pish pish Iron is 3910 Credits\n" +
      "how much is unknownSymbol tegj glob glob ?\n" +
      "how many Credits is glob prok Silver ?\n" +
      "how many Credits is glob prok Gold ?\n" +
      "how many Credits is glob prok Iron ?\n";
    String expected = "Sorry, I cannot find one of the symbol\n" +
      "glob prok Silver is 68 Credits\n" +
      "glob prok Gold is 57800 Credits\n" +
      "glob prok Iron is 782 Credits\n";

    programLoop.run(new StringReader(input), printStream);

    String output = new String(baos.toByteArray(), StandardCharsets.UTF_8);
    assertThat(output,is(equalTo(expected)));
  }

  @Test
  public void fullTextWithSymbolNotFoundInMetalQuery()
  {
    String input = "glob is I\n" +
      "prok is V\n" +
      "pish is X\n" +
      "tegj is L\n" +
      "glob glob Silver is 34 Credits\n" +
      "glob prok Gold is 57800 Credits\n" +
      "pish pish Iron is 3910 Credits\n" +
      "how much is pish tegj glob glob ?\n" +
      "how many Credits is unknown prok Silver ?\n" +
      "how many Credits is glob prok Gold ?\n" +
      "how many Credits is glob prok Iron ?\n";
    String expected = "pish tegj glob glob is 42\n" +
      "Sorry, I cannot find one of the symbol\n" +
      "glob prok Gold is 57800 Credits\n" +
      "glob prok Iron is 782 Credits\n";

    programLoop.run(new StringReader(input), printStream);

    String output = new String(baos.toByteArray(), StandardCharsets.UTF_8);
    assertThat(output,is(equalTo(expected)));
  }

  @Test
  public void fullTextWithGoldMetalUndeclared()
  {
    String input = "glob is I\n" +
      "prok is V\n" +
      "pish is X\n" +
      "tegj is L\n" +
      "glob glob Silver is 34 Credits\n" +
      "pish pish Iron is 3910 Credits\n" +
      "how much is pish tegj glob glob ?\n" +
      "how many Credits is glob prok Silver ?\n" +
      "how many Credits is glob prok Gold ?\n" +
      "how many Credits is glob prok Iron ?\n";
    String expected = "pish tegj glob glob is 42\n" +
      "glob prok Silver is 68 Credits\n" +
      "Sorry, I cannot find the metal\n" +
      "glob prok Iron is 782 Credits\n";

    programLoop.run(new StringReader(input),printStream);

    String output = new String(baos.toByteArray(), StandardCharsets.UTF_8);
    assertThat(output,is(equalTo(expected)));
  }

  @Test
  public void fullTextWithDecimalInput()
  {
    String input = "glob is I\n" +
      "prok is V\n" +
      "pish is X\n" +
      "tegj is L\n" +
      "glob glob Silver is 34.5 Credits\n" +
      "pish pish Iron is 3910 Credits\n" +
      "how much is pish tegj glob glob ?\n" +
      "how many Credits is glob prok Silver ?\n" +
      "how many Credits is glob prok Iron ?\n";
    String expected = "pish tegj glob glob is 42\n" +
      "glob prok Silver is 69 Credits\n" +
      "glob prok Iron is 782 Credits\n";

    programLoop.run(new StringReader(input),printStream);

    String output = new String(baos.toByteArray(), StandardCharsets.UTF_8);
    assertThat(output,is(equalTo(expected)));
  }
}
