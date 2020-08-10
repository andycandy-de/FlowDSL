package de.andycandy.flow

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

final class TestUtil {
	
	static File getUnreachableDir() {
		
		if (System.getProperty("os.name").startsWithIgnoreCase('windows')) {
			return new File('ABC:/test/')
		}
		else {
			return new File('/root/anyDir/')
		}
	}
	
	static Path createTemp(String... paths) {
		
		Path temp = Files.createTempDirectory('test')
		Files.createDirectories(temp)
		
		paths.each {
			Thread.currentThread().getContextClassLoader().getResourceAsStream(it).withCloseable { inStream -> 
				def dest = new File(temp.toFile(), getFileName(it))
				dest.createNewFile()
				dest.newOutputStream().withCloseable { outStream ->
					outStream << inStream
				}
			}
		}
		
		return temp
	}
	
	static String getFileName(String path) {
		def splitted = path.split(Pattern.quote('/'))
		return splitted[splitted.length - 1]
	}
	
	static Path createTemp() {
		
		Path temp = Files.createTempDirectory('test')
		Files.createDirectories(temp)
		
		return temp
	}
	
	static void deleteTempDir(Path temp) {
		
		Files.walk(temp) \
			.sorted(Comparator.reverseOrder()) \
			.map { it.toFile() } \
			.forEach { it.delete() }
	}
	
}
