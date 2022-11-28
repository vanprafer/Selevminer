package selevminer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import minerful.concept.ProcessModel;
import minerful.io.ProcessModelLoader;
import minerful.io.params.InputModelParameters.InputEncoding;

class SearchFileByWildcard {
    static List<Path> matchesList = new ArrayList<Path>();
    List<Path> searchWithWc(Path rootDir, final String pattern) throws IOException {
        matchesList.clear();
        FileVisitor<Path> matcherVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attribs) throws IOException {
                FileSystem fs = FileSystems.getDefault();
                PathMatcher matcher = fs.getPathMatcher(pattern);
                Path name = file.toAbsolutePath();
                if (matcher.matches(name)) {
                    matchesList.add(name);
                }
	        return FileVisitResult.CONTINUE;
            }
        };
        Files.walkFileTree(rootDir, matcherVisitor);
        return matchesList;
    }
}

public class Metrics {

	public static void main(String[] args) throws IOException {
		ProcessModelLoader io = new ProcessModelLoader();
		
		String path = "C:\\Users\\Vanessa\\Desktop\\modelos\\Experimentacion Selevminer 2";
		SearchFileByWildcard sfbw = new SearchFileByWildcard();
		List<Path> actual = sfbw.searchWithWc(Paths.get(path), "glob:**.xml");
		
		for (Path p: actual) {
			String newFilePath = p.getParent().toString() + "\\" + p.getFileName().toString() + "extra.txt";
			ProcessModel model = io.loadProcessModel(InputEncoding.DECLARE_MAP, new File(p.toString()));
			
			FileWriter writer = new FileWriter(new File(newFilePath));
			writer.write(model.howManyConstraints() + " " + model.howManyTasks());
			writer.close();
		}
	}

}
