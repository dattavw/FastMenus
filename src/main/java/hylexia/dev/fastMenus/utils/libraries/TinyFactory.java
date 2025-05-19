package hylexia.dev.fastMenus.utils.libraries;

import lombok.Getter;

public class TinyFactory {
    
    public static String generate(String message) {
        if (message == null || message.equals("")) {
            return "";
        }

        int length = message.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = message.charAt(i);
            TinyLetter tinyLetter = TinyLetter.getTinyLetter(c);
            if (tinyLetter != null) {
                sb.append(tinyLetter.getValue());
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    @Getter
    public enum TinyLetter {
        A('a', "ᴀ"),
        B('b', "ʙ"),
        C('c', "ᴄ"),
        D('d', "ᴅ"),
        E('e', "ᴇ"),
        F('f', "ꜰ"),
        G('g', "ɢ"),
        H('h', "ʜ"),
        I('i', "ɪ"),
        J('j', "ᴊ"),
        K('k', "ᴋ"),
        L('l', "ʟ"),
        M('m', "ᴍ"),
        N('n', "ɴ"),
        Ñ('ñ', "ɴ̃"),
        O('o', "ᴏ"),
        P('p', "ᴘ"),
        Q('q', "ǫ"),
        R('r', "ʀ"),
        S('s', "ꜱ"),
        T('t', "ᴛ"),
        U('u', "ᴜ"),
        V('v', "ᴠ"),
        W('w', "ᴡ"),
        X('x', "x"),
        Y('y', "ʏ"),
        Z('z', "ᴢ");

        private final char character;
        private final String value;

        TinyLetter(char character, String value) {
            this.character = character;
            this.value = value;
        }
        
        public static TinyLetter getTinyLetter(char c) {
            for (TinyLetter tinyLetter : TinyLetter.values()) {
                if (tinyLetter.getCharacter() == c) {
                    return tinyLetter;
                }
            }
            return null;
        }
    }
}
