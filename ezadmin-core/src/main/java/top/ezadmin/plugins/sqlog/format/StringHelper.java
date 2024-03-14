/*
 * Hibernate, Relational Persistence for Idiomatic Java Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as indicated by the @author tags or express copyright attribution statements applied by the authors. All
 * third-party contributions are distributed under license by Red Hat Middleware LLC. This copyrighted material is made
 * available to anyone wishing to use, modify, copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this distribution; if not, write to: Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor Boston, MA 02110-1301 USA
 */
package top.ezadmin.plugins.sqlog.format;

import java.util.Arrays;
import java.util.Iterator;
import java.util.StringTokenizer;

public final class StringHelper {

	private static final int ALIAS_TRUNCATE_LENGTH = 10;
	public static final String WHITESPACE = " \n\r\f\t";

	private StringHelper() { /* static methods only - hide constructor */
	}

	public static String join(String seperator, String[] strings) {
		int length = strings.length;
		if (length == 0)
			return "";
		StringBuffer buf = new StringBuffer(length * strings[0].length()).append(strings[0]);
		for (int i = 1; i < length; i++) {
			buf.append(seperator).append(strings[i]);
		}
		return buf.toString();
	}

	public static String join(String seperator, Iterator objects) {
		StringBuffer buf = new StringBuffer();
		if (objects.hasNext())
			buf.append(objects.next());
		while (objects.hasNext()) {
			buf.append(seperator).append(objects.next());
		}
		return buf.toString();
	}

	public static String[] add(String[] x, String sep, String[] y) {
		String[] result = new String[x.length];
		for (int i = 0; i < x.length; i++) {
			result[i] = x[i] + sep + y[i];
		}
		return result;
	}

	public static String repeat(String string, int times) {
		StringBuffer buf = new StringBuffer(string.length() * times);
		for (int i = 0; i < times; i++)
			buf.append(string);
		return buf.toString();
	}

	public static String repeat(char character, int times) {
		char[] buffer = new char[times];
		Arrays.fill(buffer, character);
		return new String(buffer);
	}

	public static String replace(String template, String placeholder, String replacement) {
		return replace(template, placeholder, replacement, false);
	}

	public static String[] replace(String templates[], String placeholder, String replacement) {
		String[] result = new String[templates.length];
		for (int i = 0; i < templates.length; i++) {
			result[i] = replace(templates[i], placeholder, replacement);
		}
		return result;
	}

	public static String replace(String template, String placeholder, String replacement, boolean wholeWords) {
		if (template == null) {
			return template; // returnign null!
		}
		int loc = template.indexOf(placeholder);
		if (loc < 0) {
			return template;
		} else {
			final boolean actuallyReplace = !wholeWords || loc + placeholder.length() == template.length()
					|| !Character.isJavaIdentifierPart(template.charAt(loc + placeholder.length()));
			String actualReplacement = actuallyReplace ? replacement : placeholder;
			return new StringBuffer(template.substring(0, loc)).append(actualReplacement).append(
					replace(template.substring(loc + placeholder.length()), placeholder, replacement, wholeWords))
					.toString();
		}
	}

	public static String[] split(String seperators, String list) {
		return split(seperators, list, false);
	}

	public static String[] split(String seperators, String list, boolean include) {
		StringTokenizer tokens = new StringTokenizer(list, seperators, include);
		String[] result = new String[tokens.countTokens()];
		int i = 0;
		while (tokens.hasMoreTokens()) {
			result[i++] = tokens.nextToken();
		}
		return result;
	}

	/**
	 * Collapses a name. Mainly intended for use with classnames, where an example
	 * might serve best to explain. Imagine you have a class named
	 * <samp>'org.hibernate.util.StringHelper'</samp>; calling collapse on that
	 * classname will result in <samp>'o.h.u.StringHelper'<samp>.
	 * 
	 * @param name
	 *            The name to collapse.
	 * @return The collapsed name.
	 */
	public static String collapse(String name) {
		if (name == null) {
			return null;
		}
		int breakPoint = name.lastIndexOf('.');
		if (breakPoint < 0) {
			return name;
		}
		return collapseQualifier(name.substring(0, breakPoint), true) + name.substring(breakPoint); // includes last '.'
	}

	/**
	 * Given a qualifier, collapse it.
	 * 
	 * @param qualifier
	 *            The qualifier to collapse.
	 * @param includeDots
	 *            Should we include the dots in the collapsed form?
	 * @return The collapsed form.
	 */
	public static String collapseQualifier(String qualifier, boolean includeDots) {
		StringTokenizer tokenizer = new StringTokenizer(qualifier, ".");
		String collapsed = Character.toString(tokenizer.nextToken().charAt(0));
		while (tokenizer.hasMoreTokens()) {
			if (includeDots) {
				collapsed += '.';
			}
			collapsed += tokenizer.nextToken().charAt(0);
		}
		return collapsed;
	}


	public static String[] suffix(String[] columns, String suffix) {
		if (suffix == null)
			return columns;
		String[] qualified = new String[columns.length];
		for (int i = 0; i < columns.length; i++) {
			qualified[i] = suffix(columns[i], suffix);
		}
		return qualified;
	}

	private static String suffix(String name, String suffix) {
		return (suffix == null) ? name : name + suffix;
	}

	public static String root(String qualifiedName) {
		int loc = qualifiedName.indexOf(".");
		return (loc < 0) ? qualifiedName : qualifiedName.substring(0, loc);
	}


	public static String toString(Object[] array) {
		int len = array.length;
		if (len == 0)
			return "";
		StringBuffer buf = new StringBuffer(len * 12);
		for (int i = 0; i < len - 1; i++) {
			buf.append(array[i]).append(", ");
		}
		return buf.append(array[len - 1]).toString();
	}


	public static boolean isNotEmpty(String string) {
		return string != null && string.length() > 0;
	}

	public static boolean isEmpty(String string) {
		return string == null || string.length() == 0;
	}


	public static String toLowerCase(String str) {
		return str == null ? null : str.toLowerCase();
	}



}
