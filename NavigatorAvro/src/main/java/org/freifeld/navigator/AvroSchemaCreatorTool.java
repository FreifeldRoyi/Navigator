package org.freifeld.navigator;

import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import org.apache.commons.cli.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.freifeld.navigator.AvroSchemaCreatorTool.SchemaCreatorArgs.*;

/**
 * @author royif
 * @since 04/03/17
 */
public class AvroSchemaCreatorTool
{
	public static void main(String[] args)
	{
		Options options = initOptions();
		CommandLineParser parser = new DefaultParser();

		try
		{
			CommandLine cmd = parser.parse(options, args);
			if (shouldPrintUsage(cmd))
			{
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("avro-schema-creator -c <class> -d directory", options);
				System.exit(1);
			}

			//TODO validate the filename
			String outputFileName = cmd.hasOption(FILE.getName()) ? cmd.getOptionValue(FILE.getName()) : cmd.getOptionValue(DIRECTORY.getName()) + "/schema.avsc";

			try
			{
				URL url = Paths.get(cmd.getOptionValue(DIRECTORY.getName())).toUri().toURL();
				URL[] urls = new URL[] { url };
				URLClassLoader loader = new URLClassLoader(urls);
				Class<?> cls = loader.loadClass(cmd.getOptionValue(CLASS_NAME.getName()));
				loader.close();
				Schema schema = ReflectData.get().getSchema(cls);

				ObjectMapper mapper = new ObjectMapper();
				JsonParser jsonParser = mapper.getJsonFactory().createJsonParser(schema.toString());
				JsonNode jsonNode = mapper.readTree(jsonParser);
				String schemaStr = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode) + "\n";
				Files.write(Paths.get(outputFileName), schemaStr.getBytes());
				System.out.println("Done. The schema file is located in " + outputFileName);
			}
			catch (ClassNotFoundException | IOException e)
			{
				//TODO logs
				e.printStackTrace();
			}

		}
		catch (ParseException e)
		{
			//TODO logs
			e.printStackTrace();
		}
	}

	private static boolean shouldPrintUsage(CommandLine cmd)
	{
		return cmd.hasOption(HELP.getName()) || !cmd.hasOption(CLASS_NAME.getName()) || !cmd.hasOption(DIRECTORY.getName());
	}

	private static Options initOptions()
	{
		Options toReturn = new Options();
		toReturn.addOption(CLASS_NAME.getShortName(), CLASS_NAME.getName(), true, "The full class name to create schema for");
		toReturn.addOption(DIRECTORY.getShortName(), DIRECTORY.getName(), true, "Class directory path");
		toReturn.addOption(FILE.getShortName(), FILE.getName(), true, "Schema output file");
		toReturn.addOption(HELP.getShortName(), HELP.getName(), false, "Prints this message");

		return toReturn;
	}

	public enum SchemaCreatorArgs
	{
		HELP("help", "h"),
		DIRECTORY("directory", "d"),
		CLASS_NAME("class-name", "c"),
		FILE("output-file", "f");

		private String name;
		private String shortName;

		SchemaCreatorArgs(String name, String shortName)
		{
			this.name = name;
			this.shortName = shortName;
		}

		public String getName()
		{
			return this.name;
		}

		public String getShortName()
		{
			return this.shortName;
		}

		@Override
		public String toString()
		{
			return this.getName();
		}
	}
}
