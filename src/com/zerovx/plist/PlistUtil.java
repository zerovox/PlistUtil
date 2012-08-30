package com.zerovx.plist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PlistUtil {
	public static Map<String, Object> plistToMap(String path) {
		File file = new File(path);
		Map<String, Object> ret = null;
		try {
			ret = plistToMap(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public static Map<String, Object> plistToMap(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		Reader reader = new InputStreamReader(fis);
		PushbackReader pbr = new PushbackReader(reader, 2);
		consumeHeader(pbr);
		skipTag(pbr, "plist");
		Object ret = parseValue(pbr);
		skipTag(pbr, "/plist");
		Map<String, Object> rets = new HashMap<String, Object>();
		rets.put("plist", ret);
		return rets;
	}

	private static void skipTag(PushbackReader reader, String toSkip) throws IOException {
		String tag;
		if (!(tag = nextTag(reader)).startsWith(toSkip))
			throw new RuntimeException(tag);
	}

	// Data, Date and String all use String. I'm using starts with here in case tags ever have attributes. They shouldn't.
	private static Object parseValue(PushbackReader reader) throws IOException {
		String tag = nextTag(reader);
		if (tag.startsWith("dict"))
			return parseDict(reader);
		if (tag.startsWith("array"))
			return parseArray(reader);
		if (tag.startsWith("data"))
			return parseString(reader);
		if (tag.startsWith("date"))
			return parseString(reader);
		if (tag.startsWith("real"))
			return Double.parseDouble(parseString(reader));
		if (tag.startsWith("integer"))
			return Long.parseLong(parseString(reader));
		if (tag.startsWith("string"))
			return parseString(reader);
		if (tag.startsWith("true"))
			return Boolean.TRUE;
		if (tag.startsWith("false"))
			return Boolean.FALSE;
		throw new RuntimeException();
	}

	private static String parseString(PushbackReader reader) throws IOException {
		String string = parseInner(reader);
		nextTag(reader);
		return string;
	}

	private static List<Object> parseArray(PushbackReader reader) throws IOException {
		List<Object> array = new LinkedList<Object>();
		while (!arrayFinished(reader)) {
			array.add(parseValue(reader));
		}
		skipTag(reader, "/array");
		return array;
	}

	private static boolean arrayFinished(PushbackReader reader) throws IOException {
		while (next(reader) != '<') {
		}
		char b = next(reader);
		boolean result = (b == '/');
		reader.unread(b);
		reader.unread('<');
		return result;
	}

	private static Map<String, Object> parseDict(PushbackReader reader) throws IOException {
		Map<String, Object> dict = new HashMap<String, Object>();
		while (nextTag(reader).startsWith("key")) {
			String s = parseInner(reader);
			skipTag(reader, "/key");
			dict.put(s, parseValue(reader));
		}
		return dict;
	}

	private static String parseInner(PushbackReader reader) throws IOException {
		char r;
		String inner = "";
		while ((r = next(reader)) != '<') {
			inner += r;
		}
		reader.unread('<');
		return inner;
	}

	private static void consumeHeader(PushbackReader reader) throws IOException {
		while (next(reader) != '<') {
		}
		char c = (char) reader.read();
		while (c == '!' || c == '?') {
			while (next(reader) != '>') {
			}
			while (next(reader) != '<') {
			}
			c = (char) reader.read();
		}

		reader.unread(c);
		reader.unread('<');
	}

	private static String nextTag(PushbackReader reader) throws IOException {
		while (next(reader) != '<') {
		}
		char r;
		String tag = "";
		while ((r = next(reader)) != '>') {
			tag = tag + r;
		}
		if (tag.startsWith("!--"))
			return nextTag(reader);
		return tag;
	}

	private static char next(PushbackReader reader) throws IOException {
		int r = reader.read();
		if (r == -1)
			throw new IOException();
		return (char) r;
	}

	public static void main(String[] args) {
		if (args.length == 0)
			plistToMap("C:\\itml.xml");
		else
			plistToMap(args[1]);
	}

}
