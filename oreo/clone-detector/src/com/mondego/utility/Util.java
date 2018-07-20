/**
 * 
 */
package com.mondego.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mondego.indexbased.SearchManager;
import com.mondego.models.Bag;
import com.mondego.models.TokenFrequency;
import com.mondego.models.TokenInfo;

/**
 * @author vaibhavsaini
 * 
 */
public class Util {
    static Random rand = new Random(5);
    public static final String CSV_DELIMITER = "~";
    public static final String INDEX_DIR = SearchManager.ROOT_DIR + "index";
    public static final String INDEX_DIR_TEMP = SearchManager.ROOT_DIR + "index_temp";
    public static final String GTPM_DIR = SearchManager.ROOT_DIR + "gtpm";
    public static final String GLOBAL_WFM_DIR = SearchManager.ROOT_DIR + "wfm";
    public static final String FWD_INDEX_DIR = SearchManager.ROOT_DIR + "fwdindex";
    public static final String FWD_INDEX_DIR_TEMP = SearchManager.ROOT_DIR + "fwdindex_temp";
    public static final String GTPM_INDEX_DIR = SearchManager.ROOT_DIR + "gtpmindex";
    public static final String INDEX_DIR_NO_FILTER = SearchManager.ROOT_DIR + "index_nofilter";
    public static final String QUERY_FILE_NAME = "blocks.file";
    public static final String OUTPUT_BACKUP_DIR = SearchManager.ROOT_DIR + "backup_output";
    public static final String SEARCH_METADATA = SearchManager.ROOT_DIR + "search_metadata.txt";
    public static final String RUN_METADATA = SearchManager.ROOT_DIR + "run_metadata.scc";
    public static List<String> METRICS_ORDER_IN_INPUT_FILE = Arrays.asList("num_tokens", "num_unique_tokens");
    // , "num_separators","num_assignments","num_statements","num_expressions");
    private static final Logger logger = LogManager.getLogger(Util.class);

    public static final int DIRECTORY = 0;
    public static final int FILE = 1;
    public static final int START_LINE = 2;
    public static final int END_LINE = 3;
    public static final int METHOD_NAME = 4;
    public static final int NUM_TOKENS = 5;
    public static final int NUM_UNIQUE_TOKENS = 6;
    public static final int METRIC_HASH = 7;
    public static final int PARENT_ID = 8;
    public static final int BLOCK_ID = 9;
    /*
     * public static final int COMP =0; public static final int NOS =1; public
     * static final int HVOC =2; public static final int HEFF =3; public static
     * final int CREF =4; public static final int XMET =5; public static final
     * int LMET =6; public static final int NOA =7; public static final int HDIF
     * =8; public static final int VDEC =9; public static final int EXCT =10;
     * public static final int EXCR =11; public static final int CAST =12;
     * public static final int NAND =13; public static final int VREF =14;
     * public static final int NOPR =15; public static final int MDN =16; public
     * static final int NEXP =17; public static final int LOOP =18; public
     * static final int NBLTRL =19; public static final int NCLTRL =20; public
     * static final int NNLTRL =21; public static final int NNULLTRL=22; public
     * static final int NSLTRL =23;
     * 
     * 
     * COMP~~NOS~~HVOC~~HEFF~~CREF~~XMET~~LMET~~NOA~~HDIF~~VDEC~~EXCT~~EXCR~~
     * CAST~~NAND~~VREF~~NOPR~~MDN~~NEXP~~LOOP~~NBLTRL~~NCLTRL~~NNLTRL~~NNULLTRL
     * ~~NSLTRL
     * 
     */

    /**
     * generates a random integer
     * 
     * @return
     */
    public static int getRandomNumber(int max, int min) {
        return rand.nextInt((max - min) + 1) + min;
    }

    /**
     * writes the given text to a file pointed by pWriter
     * 
     * @param pWriter
     *            handle to printWriter to write to a file
     * @param text
     *            text to be written in the file
     * @param isNewline
     *            whether to start from a newline or not
     * @throws IOException
     */
    public static void writeToFile(Writer pWriter, final String text, final boolean isNewline)
            throws IOException {
        synchronized (pWriter) {
            if (isNewline) {
                pWriter.write(text + System.lineSeparator());
            } else {
                pWriter.write(text);
            }
            /*try {
                pWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }

    /**
     * opens the outputfile for reporting clones
     * 
     * @param filename
     * @throws IOException
     * @return PrintWriter
     */
    public static Writer openFile(String filename, boolean append) throws IOException {
        synchronized (SearchManager.lock) {
            try {
                Writer pWriter = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(filename, append), "UTF-8"),
                        SearchManager.properties.getInt("DEFAULT_BUFFERED_WRITER_SIZE"));
                return pWriter;

            } catch (IOException e) {
                // IO exception caught
                logger.error("error while opening file: " + filename, e);
                // System.err.println(e.getMessage());
                throw e;
            }
        }
    }

    /**
     * closes the outputfile
     */
    public static void closeOutputFile(Writer pWriter) {
        if (null != pWriter) {
            try {
                pWriter.flush();
            } catch (IOException e) {
            }
            try {
                pWriter.close();
            } catch (IOException e) {
            }
        }

    }

    public static boolean createDirs(String dirname) {
        File dir = new File(dirname);
        if (!dir.exists()) {
            logger.info("creating directory: " + dirname);
            return dir.mkdirs();
        } else {
            return true;
        }
    }

    public static boolean isSatisfyPosFilter(int similarity, int querySize, int termsSeenInQueryBlock,
            int candidateSize, int termsSeenInCandidate, int computedThreshold) {
        return computedThreshold <= similarity
                + Math.min(querySize - termsSeenInQueryBlock, candidateSize - termsSeenInCandidate);
    }

    public static void writeJsonStream(String filename, Map<String, Integer> gtpm) {
        Writer writer = null;
        try {
            writer = Util.openFile(filename, false);
            Gson gson = new GsonBuilder().create();
            String text = gson.toJson(gtpm);
            Util.writeToFile(writer, text, false);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String debug_thread() {
        return "  Thread_id: " + Thread.currentThread().getId() + " Thread_name: " + Thread.currentThread().getName();
    }

    public static Map<String, Integer> readJsonStream(String filename) {

        BufferedReader br = null;
        Map<String, Integer> gtpm = new HashMap<String, Integer>();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"), 1024 * 1024 * 512);
            String line;
            while ((line = br.readLine()) != null && line.trim().length() > 0) {
                Gson gson = new GsonBuilder().create();
                Type type = new TypeToken<Map<String, Integer>>() {
                }.getType();
                gtpm = gson.fromJson(line, type);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return gtpm;

    }

    public static <K, V> Map<K, V> lruCache(final int maxSize) {
        return Collections.synchronizedMap(new LinkedHashMap<K, V>(maxSize * 4 / 3, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxSize;
            }
        });
    }

    // This cache is shared by all threads that call sortBag
    public final static Map<String, Long> cache = lruCache(500000 * 2 * 12);

    public static void sortBag(final Bag bag) {
        List<TokenFrequency> bagAsList = new ArrayList<TokenFrequency>(bag);
        logger.debug("bag to sort: " + bag);
        try {
            Collections.sort(bagAsList, new Comparator<TokenFrequency>() {
                public int compare(TokenFrequency tfFirst, TokenFrequency tfSecond) {
                    Long frequency1 = 0l;
                    Long frequency2 = 0l;
                    String k1 = tfFirst.getToken().getValue();
                    String k2 = tfSecond.getToken().getValue();
                    if (cache.containsKey(k1)) {
                        frequency1 = cache.get(k1);
                        if (null == frequency1) {
                            logger.warn("freq1 null from cache");
                            frequency1 = SearchManager.gtpmSearcher.getFrequency(k1);
                            cache.put(k1, frequency1);
                        }
                    } else {
                        frequency1 = SearchManager.gtpmSearcher.getFrequency(k1);
                        cache.put(k1, frequency1);
                    }
                    if (cache.containsKey(k2)) {
                        frequency2 = cache.get(k2);
                        if (null == frequency2) {
                            logger.warn("freq2 null from cache");
                            frequency2 = SearchManager.gtpmSearcher.getFrequency(k2);
                            cache.put(k2, frequency2);
                        }
                    } else {
                        frequency2 = SearchManager.gtpmSearcher.getFrequency(k2);
                        cache.put(k2, frequency2);
                    }
                    if (null == frequency1 || null == frequency2) {
                        logger.warn("k1:" + k1 + " frequency1: " + frequency1 + ", k2: " + k2 + " frequency2: "
                                + frequency2 + "bag: " + bag);
                    }
                    int result = frequency1.compareTo(frequency2);
                    if (result == 0) {
                        return k1.compareTo(k2);
                    } else {
                        return result;
                    }
                }
            });
            bag.clear();
            for (TokenFrequency tf : bagAsList) {
                bag.add(tf);
            }
        } catch (NullPointerException e) {
            logger.error("NPE caught while sorting, ", e);
            SearchManager.FATAL_ERROR = true;
        }
    }

    public static List<Entry<String, TokenInfo>> sortList(final List<Entry<String, TokenInfo>> listOfTokens) {
        try {
            Collections.sort(listOfTokens, new Comparator<Entry<String, TokenInfo>>() {
                @Override
                public int compare(Entry<String, TokenInfo> o1, Entry<String, TokenInfo> o2) {
                    Long frequency1 = 0l;
                    Long frequency2 = 0l;
                    String k1 = o1.getKey();
                    String k2 = o2.getKey();
                    if (cache.containsKey(k1)) {
                        frequency1 = cache.get(k1);
                        if (null == frequency1) {
                            logger.warn("freq1 null from cache");
                            frequency1 = SearchManager.gtpmSearcher.getFrequency(k1);
                            cache.put(k1, frequency1);
                        }
                    } else {
                        frequency1 = SearchManager.gtpmSearcher.getFrequency(k1);
                        cache.put(k1, frequency1);
                    }
                    if (cache.containsKey(k2)) {
                        frequency2 = cache.get(k2);
                        if (null == frequency2) {
                            logger.warn("freq2 null from cache");
                            frequency2 = SearchManager.gtpmSearcher.getFrequency(k2);
                            cache.put(k2, frequency2);
                        }
                    } else {
                        frequency2 = SearchManager.gtpmSearcher.getFrequency(k2);
                        cache.put(k2, frequency2);
                    }
                    if (null == frequency1 || null == frequency2) {
                        logger.warn("k1:" + k1 + " frequency1: " + frequency1 + ", k2: " + k2 + " frequency2: "
                                + frequency2);
                    }
                    int result = frequency1.compareTo(frequency2);
                    if (result == 0) {
                        return k1.compareTo(k2);
                    } else {
                        return result;
                    }
                }
            });
        } catch (NullPointerException e) {
            logger.error("NPE caught while sorting, ", e);
            SearchManager.FATAL_ERROR = true;
        }
        return listOfTokens;
    }

    /*
     * public static int getMinimumSimilarityThreshold(QueryBlock
     * queryBlock,float threshold) { return (int) Math.ceil((threshold *
     * queryBlock.getSize())/ (SearchManager.MUL_FACTOR*10)); } public static
     * int getMinimumSimilarityThreshold(Bag bag,float threshold) { return (int)
     * Math.ceil((threshold * bag.getSize())/ (SearchManager.MUL_FACTOR*10)); }
     */

    public static void writeMapToFile(String filename, Map<String, Long> map) {
        // TODO Auto-generated method stub
        Writer writer = null;
        try {
            logger.debug("writing to : " + filename);
            writer = Util.openFile(filename, false);
            for (Entry<String, Long> entry : map.entrySet()) {
                String text = entry.getKey() + ":" + entry.getValue();
                Util.writeToFile(writer, text, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<String, Long> readMapFromFile(String filename) {
        // TODO Auto-generated method stub

        BufferedReader br = null;
        Map<String, Long> gtpm = new HashMap<String, Long>();
        try {
            logger.debug("here " + filename);
            File f = new File(filename);
            logger.debug("file is " + f);
            FileInputStream fis = new FileInputStream(f);
            logger.debug(fis);
            InputStreamReader ir = new InputStreamReader(fis, "UTF-8");
            logger.debug(ir);
            br = new BufferedReader(ir);
            logger.debug(br);
            // br = new BufferedReader(new InputStreamReader(new
            // FileInputStream(
            // new File(filename)), "UTF-8"), 1024 * 1024 * 512);

            logger.debug("hi");
            String line;
            while ((line = br.readLine()) != null && line.trim().length() > 0) {
                String[] keyValPair = line.split(":");
                gtpm.put(keyValPair[0], Long.parseLong(keyValPair[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return gtpm;

    }

    public static void populateProcessedWFMSet(String filename, Set<String> processedWFMset) {
        BufferedReader br = null;
        File f = new File(filename);
        logger.debug("file is " + f);
        FileInputStream fis;
        try {
            fis = new FileInputStream(f);
            logger.debug(fis);
            InputStreamReader ir = new InputStreamReader(fis, "UTF-8");
            logger.debug(ir);
            br = new BufferedReader(ir);
            String line;
            while ((line = br.readLine()) != null && line.trim().length() > 0) {
                processedWFMset.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedReader getReader(File queryFile) throws FileNotFoundException {
        BufferedReader br = null;
        br = new BufferedReader(new FileReader(queryFile));
        return br;
    }

    public static Writer openFile(File output, boolean append) throws IOException {
        // TODO Auto-generated method stub
        Writer pWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output, append), "UTF-8"));
        return pWriter;
    }

    public static List<File> getAllFilesRecur(File root) {
        List<File> listToReturn = new ArrayList<File>();
        for (File file : root.listFiles()) {
            if (file.isFile()) {
                listToReturn.add(file);
            } else if (file.isDirectory()) {
                listToReturn.addAll(getAllFilesRecur(file));
            }
        }
        return listToReturn;

    }
    /*
     * public static int getPrefixSize(QueryBlock queryBlock, float threshold) {
     * int prefixSize = (queryBlock.getSize() + 1) - computedThreshold;//
     * this.computePrefixSize(maxLength); return prefixSize; }
     */
    /*
     * public static int getPrefixSize(Bag bag, float threshold) { int
     * computedThreshold = getMinimumSimilarityThreshold(bag, threshold); int
     * prefixSize = (bag.getSize() + 1) - computedThreshold;//
     * this.computePrefixSize(maxLength); return prefixSize; }
     */
}
