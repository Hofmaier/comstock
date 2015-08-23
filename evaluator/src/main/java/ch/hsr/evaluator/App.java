package ch.hsr.evaluator;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
	public static void main(String[] args) {

		@SuppressWarnings("static-access")
		Option ratingfile = OptionBuilder
				.withArgName("file")
				.hasArg()
				.isRequired()
				.withDescription("use ratings in given csv file as trainingset")
				.create("ratings");

		@SuppressWarnings("static-access")
		Option tagitemfile = OptionBuilder
				.withArgName("file")
				.hasArg()
				.isRequired()
				.withDescription("use ratings in given csv file as trainingset")
				.create("tags");

		@SuppressWarnings("static-access")
		Option precisionatOpt = OptionBuilder
				.withArgName("number")
				.hasArg()
				.withDescription(
						"set the size N of the top-N list. Default value is 10")
				.create("precisionat");

		@SuppressWarnings("static-access")
		Option connectionStringOpt = OptionBuilder.withArgName("url").hasArg()
				.isRequired().withDescription("url of Solr instance")
				.create("connectionString");

		Options options = new Options();
		options.addOption(ratingfile);
		options.addOption(precisionatOpt);
		options.addOption(connectionStringOpt);
		options.addOption(tagitemfile);

		CommandLineParser parser = new BasicParser();
		CommandLine cmd;
		HelpFormatter formatter = new HelpFormatter();
		try {
			cmd = parser.parse(options, args);

			String ratings = cmd.getOptionValue("ratings");
			int precisionat = cmd.hasOption("precisionat") ? Integer
					.parseInt(cmd.getOptionValue("precisionat")) : 10;
			String connectionString = cmd.getOptionValue("connectionString");
			String tags = cmd.getOptionValue("tags");
			startEvaluation(connectionString, ratings, tags, precisionat);
		} catch (ParseException e) {
			formatter.printHelp("evaluator", options);
		} catch (NumberFormatException e) {
			formatter.printHelp("evaluator", options);
		}
	}

	public static void startEvaluation(String connectionString,
			String ratingfile, String tagfile, int precisionat) {
		double evaluationPercentage = 1;
		double threshhold = -1;
		try {
			DataModel likeDataModel = new FileDataModel(new File(ratingfile));
			DataModel tagdataModel = new FileDataModel(new File(tagfile));
			RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();
			RecommenderFactory recommenderfactory = new RecommenderFactory(
					connectionString, likeDataModel, tagdataModel);
			IRStatistics itemirstats = evaluator.evaluate(
					recommenderfactory.llrRecommender(), null, likeDataModel,
					null, precisionat, threshhold, evaluationPercentage);
			// GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD

			Logger log = LoggerFactory.getLogger(App.class);
			log.info(itemirstats.toString());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (TasteException e) {
			e.printStackTrace();
		}
	}
}
