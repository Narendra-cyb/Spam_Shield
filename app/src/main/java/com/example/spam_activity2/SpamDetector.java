package com.example.spam_activity2;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class SpamDetector {
    private Map<String, Integer> spamWordsCount;
    private Map<String, Integer> hamWordsCount;
    private int spamCount;
    private int hamCount;

    public SpamDetector() {
        spamWordsCount = new HashMap<>();
        hamWordsCount = new HashMap<>();
        spamCount = 0;
        hamCount = 0;
    }

    public void train(InputStream trainingDataPath, String spamLabel) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(trainingDataPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                if (words.length < 2) {
                    continue;
                }
                String label = words[0];
                if (label.equalsIgnoreCase(spamLabel)) {
                    spamCount++;
                    for (int i = 1; i < words.length; i++) {
                        String word = words[i];
                        spamWordsCount.put(word, spamWordsCount.getOrDefault(word, 0) + 1);
                    }
                } else {
                    hamCount++;
                    for (int i = 1; i < words.length; i++) {
                        String word = words[i];
                        hamWordsCount.put(word, hamWordsCount.getOrDefault(word, 0) + 1);
                    }
                }
            }
        }
    }

    public boolean classify(String message, double threshold) {
        String[] words = message.split("\\s+");
        double spamScore = calculateScore(words, spamWordsCount, spamCount);
        double hamScore = calculateScore(words, hamWordsCount, hamCount);
        double ratio = spamScore / (spamScore + hamScore);
        return ratio > threshold;
    }

    private double calculateScore(String[] words, Map<String, Integer> wordCounts, int totalCount) {
        double score = Math.log((double) spamCount / (double) totalCount);
        for (String word : words) {
            Integer count = wordCounts.get(word);
            if (count != null) {
                score += Math.log((double) (count + 1) / (double) (spamWordsCount.size() + totalCount));
            }
        }
        return score;
    }
    private class TrainTask extends AsyncTask<InputStream, Void, Void> {
        @Override
        protected Void doInBackground(InputStream... params) {
            try {
                SpamDetector spamDetector = new SpamDetector();
                InputStream trainingDataPath = params[0];
                spamDetector.train(trainingDataPath, "spam");
            } catch (IOException e) {
                // handle exception
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // update UI if necessary
        }
    }
}

