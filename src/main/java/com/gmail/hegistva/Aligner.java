package com.gmail.hegistva;

import edu.cmu.sphinx.alignment.LongTextAligner;
import edu.cmu.sphinx.api.SpeechAligner;
import edu.cmu.sphinx.result.WordResult;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Aligner {

    private static final String TRANSCRIPT_FR = "UN ÉCUEIL FUYANT  L'année 1866 fut marquée par un événement bizarre, un phénomène inexpliqué et inexplicable que personne n'a sans doute oublié.";
    private static final String AUDIO_FILE_FR = "/home/hegistva/programming/python/language/library/20000LeaguesUnderTheSea/fra/audio/sentence.wav";

    static String readFile(Path path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, encoding);
    }

    public static void main(String[] args) throws Exception {

        String language = args[0];
        Path audioFile = Paths.get(args[1]);
        Path transcript = Paths.get(args[2]);
        Path outfile = Paths.get(args[3]);

        String trText = readFile(transcript, StandardCharsets.UTF_8); // read the transcript content
        URL audioURL = audioFile.toUri().toURL();

        SpeechAligner aligner = new SpeechAligner(Config.getModelPath(language, ModelType.ACOUSTIC), Config.getModelPath(language, ModelType.DICIONARY), null);
        List<WordResult> results = aligner.align(audioURL, trText);
        List<String> stringResults = new ArrayList<String>();

        for (WordResult wr : results) {
            stringResults.add(wr.getWord().getSpelling());
        }

        LongTextAligner textAligner = new LongTextAligner(stringResults, 2);
        List<String> sentences = aligner.getTokenizer().expand(trText);
        List<String> words = aligner.sentenceToWords(sentences);

        int[] aid = textAligner.align(words);

        int lastId = -1;

        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile.toFile()), "UTF-8"));

        try {
            for (int i = 0; i <  aid.length; ++i) {
                if (aid[i] == -1) {
                    out.write(String.format("MISSING: %s\n", words.get(i)));
                } else {
                    if (aid[i] - lastId > 1) {
                        for (WordResult result: results.subList(lastId + 1, aid[i])) {
                            out.write(String.format("EXTRA: %-25s [%s]\n", result.getWord().getSpelling(), result.getTimeFrame()));
                        }
                    }
                    out.write(String.format("MAPPED: %-25s [%s]\n", results.get(aid[i]).getWord().getSpelling(), results.get(aid[i]).getTimeFrame()));
                    lastId = aid[i];
                }
            }

            if (lastId >= 0 && results.size() - lastId > 1) {
                for (WordResult result: results.subList(lastId + 1, results.size())) {
                    out.write(String.format("EXTRA: %-25s [%s]\n", result.getWord().getSpelling(), result.getTimeFrame()));
                }
            }
        } finally {
            out.close();
        }
    }
}
