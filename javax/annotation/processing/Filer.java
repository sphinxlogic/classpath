/* Filer.java -- Manages file creation for an annotation processor.
   Copyright (C) 2012  Free Software Foundation, Inc.

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */

package javax.annotation.processing;

import java.io.IOException;

import javax.lang.model.element.Element;

import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.FileObject;

/**
 * <p>This interface supports the creation of new files by the
 * annotation processor.  Creating files via this interface means
 * that they can be tracked by the annotation processing tool.
 * Once the file streams are closed, the tool will automatically
 * consider them for processing.</p>
 * <p>Files are separated into three types: source files, class
 * files and resource files.  Two locations are defined; one
 * for source files and one for class files (resource files
 * may use either).  Locations are specified using a relative
 * path separated by {@code '/'} and not including the
 * segments {@code '.'} and {@code '..'} so that they may only
 * refer to subdirectories.  A valid relative name must match
 * the "path-rootless" rule of RFC 3986, section 3.3.</p>
 * <p>The file creation methods take a variable number of
 * originating elements, which can be used by the tool to
 * handle dependency management.  For example, if a
 * file is generated due to the presence of a particular
 * method, the element representing that method may be
 * specified as an originating element.  Whether this
 * information is used by the tool or not is left down
 * to the implementator.</p>
 * <p>Each run of the annotation processing tool may only
 * create a file with a given pathname once.  Attempts
 * to create the same file a second time will result in
 * a {@link FilerException}.  The same behaviour results
 * from trying to overwrite the initial source files, which
 * are classed as being created in the zeroth round of
 * processing.  The same exception will be thrown if
 * the same name is used for both a source file and a
 * class file.</p>
 * <p>Processors must not knowingly attempt to overwrite
 * files that weren't generated by themselves or a similar
 * tool.  Similarly, the user invoking the tool should
 * not configure it so that it will end up overwriting
 * files that weren't generated.  A {@code Filer}
 * implementation may include safeguards so as not to
 * overwrite class files such as {@code java.lang.Object}.</p>
 * <p>The {@link javax.lang.annotation.Generated}
 * annotation is available to denote generated files
 * if needed.  Some of the effect of overwriting files may
 * be achieved by using a decorator-style design
 * pattern and either a generated superclass or a series
 * of generated subclasses.  In the latter case,
 * the class would provide the appropriate generated
 * subclass via the factory pattern.</p>
 *
 * @author Andrew John Hughes (gnu_andrew@member.fsf.org)
 * @since 1.6
 */
public interface Filer
{

  /**
   * Returns a {@link JavaFileObject} for writing a class
   * file.  The name and location of the created file are
   * based on the specified type name.  The location used
   * is relative to the root location for class files.
   * A class file may be created for a package by appending
   * the suffix {@code ".package-info"} to the name.  The
   * contents of the class file should be compatible with
   * source version being used for this run of the annotation
   * processor.
   *
   * @param name the name of the type (or package if
   *             the {@code ".package-info"} suffix) to create
   *             a class file for.
   * @param elements program elements associated with this class
   *                 file.  May be {@code null} or incomplete.
   * @return access to the new class file via a {@link JavaFileObject}.
   * @throws FilerException if the same pathname has already been used,
   *                       the same type has already been created or
   *                       the name is invalid.
   * @throws IOException if an I/O error occurs.
   */
  JavaFileObject createClassFile(CharSequence name, Element... elements)
    throws IOException;

  /**
   * <p>Returns a {@link FileObject} for writing a resource file.
   * The name and location of the created file are determined
   * by combining the given location (either that used by class
   * files, source files or another location supported by the
   * implementation), the name of a package in which the resource
   * should live (if any), and the specified name.</p>
   * <p>Files created by this method are not registered for annotation
   * processing.</p>
   *
   * @param location the location to use for the resource.  Can be
   *                 {@link javax.tools.StandardLocation#CLASS_OUTPUT},
   *                 {@link javax.tools.StandardLocation#SOURCE_OUTPUT},
   *                 or another location supported by the implementation.
   * @param pkg the package which should contain the resource, or the
   *            empty string if one is not used.
   * @param relName the final path components of the file name, which will
   *                be relative to the location and (if specified) the package.
   * @param elements program elements associated with this resource
   *                 file.  May be {@code null} or incomplete.
   * @return access to the new resource file via a {@link FileObject}.
   * @throws FilerException if the same pathname has already been used.
   * @throws IOException if an I/O error occurs.
   * @throws IllegalArgumentException if the location specified is not supported,
   *                                  or {@code relName} is not relative.
   */
  FileObject createResource(JavaFileManager.Location location,
			    CharSequence pkg, CharSequence relName,
			    Element... elements)
    throws IOException;

  /**
   * <p>Returns a {@link JavaFileObject} for writing a source
   * file.  The name and location of the created file are
   * based on the specified type name.  If more than one
   * type is being declared, the top-level one should be used.
   * The location used is relative to the root location for
   * source files.  A source file may be created for a package by
   * appending the suffix {@code ".package-info"} to the name.  The
   * contents of the source file should be compatible with
   * source version being used for this run of the annotation
   * processor.</p>
   * <p>The character set used by the {@link java.io.OutputStream}
   * of the returned object is determined by the implementation,
   * and may be set using either the platform default or by an
   * option passed to the annotation processing tool.  To override
   * this, users can wrap the stream in an
   * {@link java.io.OutputStreamWriter}.</p>
   *
   * @param name the fully-qualified name of the type (or package if
   *             the {@code ".package-info"} suffix) to create
   *             a source file for.
   * @param elements program elements associated with this source
   *                 file.  May be {@code null} or incomplete.
   * @return access to the new source file via a {@link JavaFileObject}.
   * @throws FilerException if the same pathname has already been used,
   *                       the same type has already been created or
   *                       the name is invalid.
   * @throws IOException if an I/O error occurs.
   */
  JavaFileObject createSourceFile(CharSequence name, Element... elements)
    throws IOException;

  /**
   * Returns a {@link FileObject} for reading a resource file.
   * The name and location of the file to read are determined
   * by combining the given location (either that used by class
   * files, source files or another location supported by the
   * implementation), the name of a package in which the resource
   * lives (if any), and the specified name.
   *
   * @param location the location to use for the resource.  Can be
   *                 {@link javax.tools.StandardLocation#CLASS_OUTPUT},
   *                 {@link javax.tools.StandardLocation#SOURCE_OUTPUT},
   *                 or another location supported by the implementation.
   * @param pkg the package which contains the resource, or the
   *            empty string if one is not used.
   * @param relName the final path components of the file name, which will
   *                be relative to the location and (if specified) the package.
   * @return access to the new resource file via a {@link FileObject}.
   * @throws FilerException if the same pathname is open for writing.
   * @throws IOException if an I/O error occurs.
   * @throws IllegalArgumentException if the location specified is not supported,
   *                                  or {@code relName} is not relative.
   */
  FileObject getResource(JavaFileManager.Location location,
			 CharSequence pkg, CharSequence relName)
    throws IOException;

}
