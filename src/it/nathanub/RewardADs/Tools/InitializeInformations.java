package it.nathanub.RewardADs.Tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class InitializeInformations {

	public InitializeInformations() {
		File folder = new File("plugins/RewardADs");
		File configuration = new File("plugins/RewardADs/configuration.yml");
		InputStream configurationInternal = InitializeInformations.class.getResourceAsStream("/configs/configuration.yml");
		File rewards = new File("plugins/RewardADs/rewards.yml");
		InputStream rewardsInternal = InitializeInformations.class.getResourceAsStream("/configs/rewards.yml");
		File categories = new File("plugins/RewardADs/categories.yml");
		InputStream categoriesInternal = InitializeInformations.class.getResourceAsStream("/configs/categories.yml");
		StringBuilder configurationContent = new StringBuilder();
		StringBuilder rewardsContent = new StringBuilder();
		StringBuilder categoriesContent = new StringBuilder();

        if(!folder.exists()) {
            folder.mkdirs();
        }

        try {
        	Scanner configurationReader = new Scanner(configurationInternal);

            while(configurationReader.hasNextLine()) {
                String line = configurationReader.nextLine();
                configurationContent.append(line).append("\n");
            }
            
            configurationReader.close();
            
            Scanner rewardsReader = new Scanner(rewardsInternal);

            while(rewardsReader.hasNextLine()) {
                String line = rewardsReader.nextLine();
                rewardsContent.append(line).append("\n");
            }
            
            rewardsReader.close();
            
            Scanner categoriesReader = new Scanner(categoriesInternal);

            while(categoriesReader.hasNextLine()) {
                String line = categoriesReader.nextLine();
                categoriesContent.append(line).append("\n");
            }
            
            categoriesReader.close();

            if(!configuration.exists()) {
            	configuration.createNewFile();
            	
            	FileWriter configurationWriter = new FileWriter(configuration);
                configurationWriter.write(configurationContent + "");
                configurationWriter.close();
            }
            
            if(!rewards.exists()) {
            	rewards.createNewFile();
            	
            	FileWriter rewardsWriter = new FileWriter(rewards);
                rewardsWriter.write(rewardsContent + "");
                rewardsWriter.close();
            }
            
            if(!categories.exists()) {
            	categories.createNewFile();
            	
            	FileWriter categoriesWriter = new FileWriter(categories);
            	categoriesWriter.write(categoriesContent + "");
            	categoriesWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
