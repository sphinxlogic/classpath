/* UnknownTypeException.java -- Thrown by an unknown type.
   Copyright (C) 2012, 2013  Free Software Foundation, Inc.

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

package javax.lang.model.type;

import javax.lang.model.UnknownEntityException;

/**
 * Thrown when an unknown type is encountered,
 * usually by a {@link TypeVisitor}.
 *
 * @author Andrew John Hughes (gnu_andrew@member.fsf.org)
 * @since 1.6
 * @see TypeVisitor#visitUnknown(TypeMirror,P)
 */
public class UnknownTypeException
  extends UnknownEntityException
{

  private static final long serialVersionUID = 269L;

  /**
   * The unknown type.
   */
  private TypeMirror type;

  /**
   * The additional parameter.
   */
  private Object param;

  /**
   * Constructs a new {@code UnknownTypeException}
   * for the specified type.  An additional
   * object may also be passed to give further context as
   * to where the exception occurred, such as the additional parameter
   * used by visitor classes.
   *
   * @param type the unknown type or {@code null}.
   * @param param the additional parameter or {@code null}.
   */
  public UnknownTypeException(TypeMirror type, Object param)
  {
    super("The type " + type + " is not recognised.");
    this.type = type;
    this.param = param;
  }

  /**
   * Returns the additional parameter or {@code null} if
   * unavailable.
   *
   * @return the additional parameter.
   */
  public Object getArgument()
  {
    return param;
  }

  /**
   * Returns the unknown type or {@code null}
   * if unavailable.  The type may be {@code null} if
   * the value is not {@link java.io.Serializable} but the
   * exception has been serialized and read back in.
   *
   * @return the unknown type.
   */
  public TypeMirror getUnknownType()
  {
    return type;
  }


}
