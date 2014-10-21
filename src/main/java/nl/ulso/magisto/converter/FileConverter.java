/*
 * Copyright 2014 Vincent Oostindie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package nl.ulso.magisto.converter;

import nl.ulso.magisto.io.FileSystemAccessor;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Converts files from one format to another.
 * <p>
 * <strong>Important!</strong>: The file name of the converted file must overlap with the original name as much as
 * possible, otherwise the lexicographical ordering will not match, and path comparison algorithm in the Magisto class
 * will go out of whack!
 * </p>
 */
public interface FileConverter {

    boolean supports(Path path);

    Path getConvertedFileName(Path path);

    void convert(FileSystemAccessor fileSystemAccessor, Path sourceRoot, Path targetRoot, Path path) throws IOException;
}
