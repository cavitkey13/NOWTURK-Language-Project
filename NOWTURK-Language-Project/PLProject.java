import java.io.*;
import java.util.ArrayList;

public class PLProject {

    /* Global Variables */
    static int charClass;
    static StringBuilder lexeme = new StringBuilder();
    static char nextChar;
    static int token;
    static int nextToken;
    static PushbackReader in_fp;

    /* Character Classes */
    static final int HARF = 0;
    static final int RAKAM = 1;
    static final int BILINMEYEN = 99;
    static final int EOF_CODE = -1;

    /* Token Codes */
    static final int TAMSAYI_SBT = 10;
    static final int KIMLIK = 11;
    static final int ATAMA_OP = 20;
    static final int TOPLA_OP = 21;
    static final int CIKAR_OP = 22;
    static final int CARP_OP = 23;
    static final int BOL_OP = 24;
    static final int SOL_PRTZ = 25;
    static final int SAG_PRTZ = 26;
    static final int SAYI_KW = 30;
    static final int EGER_KW = 31;
    static final int DONGU_KW = 32;
    static final int HARF_KW = 33;
    static final int DEGILSE_KW = 34;
    static final int NOKTALI_V = 35;
    static final int ESIT_OP = 40; // ==
    static final int DEGIL_ESIT = 41; // !=
    static final int KUCUK_ESIT = 42; // <=
    static final int BUYUK_ESIT = 43; // >=
    static final int KUCUK_OP = 44; // <
    static final int BUYUK_OP = 45; // >
    static final int SOL_SUSLU = 50; // {
    static final int SAG_SUSLU = 51; // }
    static final int YAZDİR = 52;

    /* --- SYMBOL TABLE INFRASTRUCTURE --- */
    static class Sembol {
        String isim;
        int tip;
        Sembol(String isim, int tip) {
            this.isim = isim;
            this.tip = tip;
        }
    }
    static ArrayList<Sembol> sembolTablosu = new ArrayList<>();

    static void sembolEkle(String isim, int tip) {
        for (Sembol s : sembolTablosu) {
            if (s.isim.equals(isim)) {
                System.out.println("HATA: '" + isim + "' değişkeni zaten tanımlanmış!");
                return;
            }
        }
        sembolTablosu.add(new Sembol(isim, tip));
        System.out.println("Sembol Tablosuna Kaydedildi -> İsim: " + isim + ", Tip: " + tip);
    }

    /* lookup - Checks operators and delimiters */
    static int lookup(char ch) {
        try {
            switch (ch) {
                case '(': addChar(); nextToken = SOL_PRTZ; break;
                case ')': addChar(); nextToken = SAG_PRTZ; break;
                case '+': addChar(); nextToken = TOPLA_OP; break;
                case '-': addChar(); nextToken = CIKAR_OP; break;
                case '*': addChar(); nextToken = CARP_OP; break;
                case '/': addChar(); nextToken = BOL_OP; break;
                case ';': addChar(); nextToken = NOKTALI_V; break;
                case '=':
                    addChar();
                    int next = in_fp.read();
                    if (next == '=') {
                        nextChar = (char) next;
                        addChar();
                        nextToken = ESIT_OP;
                    } else {
                        if (next != -1) in_fp.unread(next);
                        nextToken = ATAMA_OP;
                    }
                    break;
                case '{': addChar(); nextToken = SOL_SUSLU; break;
                case '}': addChar(); nextToken = SAG_SUSLU; break;
                case '!':
                    addChar();
                    next = in_fp.read();
                    if (next == '=') {
                        nextChar = (char) next;
                        addChar();
                        nextToken = DEGIL_ESIT;
                    } else {
                        if (next != -1) in_fp.unread(next);
                        nextToken = BILINMEYEN;
                    }
                    break;
                case '<':
                    addChar();
                    next = in_fp.read();
                    if (next == '=') {
                        nextChar = (char) next;
                        addChar();
                        nextToken = KUCUK_ESIT;
                    } else {
                        if (next != -1) in_fp.unread(next);
                        nextToken = KUCUK_OP;
                    }
                    break;
                case '>':
                    addChar();
                    next = in_fp.read();
                    if (next == '=') {
                        nextChar = (char) next;
                        addChar();
                        nextToken = BUYUK_ESIT;
                    } else {
                        if (next != -1) in_fp.unread(next);
                        nextToken = BUYUK_OP;
                    }
                    break;
                default: addChar(); nextToken = EOF_CODE; break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nextToken;
    }

    /* addChar - Adds character to the lexeme string */
    static void addChar() {
        lexeme.append(nextChar);
    }

    /* getChar - Character reader with Turkish character support */
    static void getChar() {
        try {
            int r = in_fp.read();
            if (r != -1) {
                nextChar = (char) r;
                if (Character.isLetter(nextChar) || "çğıöşüÇĞİÖŞÜ".indexOf(nextChar) != -1) {
                    charClass = HARF;
                } else if (Character.isDigit(nextChar)) {
                    charClass = RAKAM;
                } else {
                    charClass = BILINMEYEN;
                }
            } else {
                charClass = EOF_CODE;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void getNonBlank() {
        while (Character.isWhitespace(nextChar)) {
            getChar();
        }
    }

    /* lex - Lexical Analyzer */
    static int lex() {
        lexeme.setLength(0); // clear lexeme
        getNonBlank();

        switch (charClass) {
            case HARF:
                addChar();
                getChar();
                while (charClass == HARF || charClass == RAKAM) {
                    addChar();
                    getChar();
                }
                String kelime = lexeme.toString();

                // Keywords are updated with full Turkish characters
                if (kelime.equals("sayı")) nextToken = SAYI_KW;
                else if (kelime.equals("eğer")) nextToken = EGER_KW;
                else if (kelime.equals("döngü")) nextToken = DONGU_KW;
                else if (kelime.equals("harf")) nextToken = HARF_KW;
                else if (kelime.equals("değilse")) nextToken = DEGILSE_KW;
                else if (kelime.equals("yazdır")) nextToken = YAZDİR;
                else nextToken = KIMLIK;
                break;
            case RAKAM:
                addChar();
                getChar();
                while (charClass == RAKAM) {
                    addChar();
                    getChar();
                }
                nextToken = TAMSAYI_SBT;
                break;
            case BILINMEYEN:
                lookup(nextChar);
                getChar();
                break;
            case EOF_CODE:
                nextToken = EOF_CODE;
                lexeme.append("EOF");
                break;
        }
        System.out.println("Sonraki token: " + nextToken + ", Sonraki kelime: " + lexeme.toString());
        return nextToken;
    }

    /* --- PARSER (SYNTAX ANALYZER) START --- */
    static void hata(String mesaj) {
        System.out.println("HATA: " + mesaj);
        System.exit(1);
    }

    static void ifade() {
        System.out.println("Giriş <ifade>");
        terim();
        while (nextToken == TOPLA_OP || nextToken == CIKAR_OP) {
            lex();
            terim();
        }
        System.out.println("Çıkış <ifade>");
    }

    static void terim() {
        System.out.println("Giriş <terim>");
        faktör();
        while (nextToken == CARP_OP || nextToken == BOL_OP) {
            lex();
            faktör();
        }
        System.out.println("Çıkış <terim>");
    }

    static void faktör() {
        System.out.println("Giriş <faktör>");
        if (nextToken == KIMLIK || nextToken == TAMSAYI_SBT) {
            lex();
        } else {
            if (nextToken == SOL_PRTZ) {
                lex();
                ifade();
                if (nextToken == SAG_PRTZ) {
                    lex();
                } else {
                    hata("Sağ parantez ')' eksik!");
                }
            } else {
                hata("Geçersiz faktör! Kimlik veya tamsayı bekleniyordu.");
            }
        }
        System.out.println("Çıkış <faktör>");
    }

    static void program() {
        System.out.println("Giriş <program>");
        if (nextToken == KIMLIK && lexeme.toString().equals("ana")) {
            lex();
            if (nextToken == SOL_PRTZ) {
                lex();
                if (nextToken == SAG_PRTZ) {
                    lex();
                    if (nextToken == SOL_SUSLU) {
                        lex();
                        komutlar();
                        if (nextToken == SAG_SUSLU) {
                            lex();
                        } else { hata("Program sonunda '}' eksik!"); }
                    } else { hata("Program başlangıcında '{' eksik!"); }
                } else { hata("ana() ifadesinde ')' eksik!"); }
            } else { hata("ana() ifadesinde '(' eksik!"); }
        } else { hata("Program 'ana()' fonksiyonu ile başlamalıdır!"); }
        System.out.println("Çıkış <program>");
    }

    static void komutlar() {
        System.out.println("Giriş <komutlar>");
        while (nextToken != SAG_SUSLU && nextToken != EOF_CODE) {
            komut();
        }
        System.out.println("Çıkış <komutlar>");
    }

    static void komut() {
        System.out.println("Giriş <komut>");
        if (nextToken == SAYI_KW || nextToken == HARF_KW) {
            tanımlama();
        } else if (nextToken == KIMLIK) {
            atama();
        } else if (nextToken == EGER_KW) {
            eğer_stmt();
        } else if (nextToken == DONGU_KW) {
            döngü_stmt();
        } else if (nextToken == YAZDİR) {
            yazdır_stmt();
        } else {
            hata("Geçersiz komut yapısı!");
            lex();
        }
        System.out.println("Çıkış <komut>");
    }

    static void tanımlama() {
        System.out.println("Giriş <tanımlama>");
        int secilenTip = nextToken;
        if (nextToken == SAYI_KW || nextToken == HARF_KW) {
            lex();
            if (nextToken == KIMLIK) {
                sembolEkle(lexeme.toString(), secilenTip);
                lex();
                if (nextToken == NOKTALI_V) {
                    lex();
                } else {
                    hata("Tanımlama sonunda ';' eksik!");
                }
            } else {
                hata("Veri tipinden sonra değişken ismi (kimlik) bekleniyor!");
            }
        } else {
            hata("Geçersiz veri tipi! 'sayı' veya 'harf' bekleniyordu.");
        }
        System.out.println("Çıkış <tanımlama>");
    }

    static void atama() {
        System.out.println("Giriş <atama>");
        if (nextToken == KIMLIK) {
            lex();
            if (nextToken == ATAMA_OP) {
                lex();
                ifade();
                if (nextToken == NOKTALI_V) {
                    lex();
                } else { hata("Atama cümlesi sonunda ';' eksik!"); }
            } else { hata("Atama operatörü '=' bekleniyor!"); }
        }
        System.out.println("Çıkış <atama>");
    }

    static void eğer_stmt() {
        System.out.println("Giriş <eğer_stmt>");
        if (nextToken == EGER_KW) {
            lex();
            if (nextToken == SOL_PRTZ) {
                lex();
                koşul();
                if (nextToken == SAG_PRTZ) {
                    lex();
                    blok();
                    if (nextToken == DEGILSE_KW) {
                        lex();
                        blok();
                    }
                } else { hata("Eğer koşulunda ')' eksik!"); }
            } else { hata("Eğer koşulunda '(' eksik!"); }
        }
        System.out.println("Çıkış <eğer_stmt>");
    }

    static void döngü_stmt() {
        System.out.println("Giriş <döngü_stmt>");
        if (nextToken == DONGU_KW) {
            lex();
            if (nextToken == SOL_PRTZ) {
                lex();
                koşul();
                if (nextToken == SAG_PRTZ) {
                    lex();
                    blok();
                } else { hata("Döngü koşulunda ')' eksik!"); }
            } else { hata("Döngü koşulunda '(' eksik!"); }
        }
        System.out.println("Çıkış <döngü_stmt>");
    }

    static void yazdır_stmt() {
        System.out.println("Giriş <yazdır_stmt>");
        if (nextToken == YAZDİR) {
            lex();
            if (nextToken == SOL_PRTZ) {
                lex();
                ifade();
                if (nextToken == SAG_PRTZ) {
                    lex();
                    if (nextToken == NOKTALI_V) {
                        lex();
                    } else { hata("Yazdır komutu sonunda ';' eksik!"); }
                } else { hata("Yazdır fonksiyonunda ')' eksik!"); }
            } else { hata("Yazdır fonksiyonunda '(' eksik!"); }
        }
        System.out.println("Çıkış <yazdır_stmt>");
    }

    static void koşul() {
        System.out.println("Giriş <koşul>");
        ifade();
        if (nextToken == ESIT_OP || nextToken == DEGIL_ESIT ||
                nextToken == KUCUK_OP || nextToken == BUYUK_OP ||
                nextToken == KUCUK_ESIT || nextToken == BUYUK_ESIT) {
            lex();
            ifade();
        } else { hata("Geçersiz karşılaştırma operatörü!"); }
        System.out.println("Çıkış <koşul>");
    }

    static void blok() {
        System.out.println("Giriş <blok>");
        if (nextToken == SOL_SUSLU) {
            lex();
            komutlar();
            if (nextToken == SAG_SUSLU) {
                lex();
            } else { hata("Blok sonunda '}' eksik!"); }
        } else { hata("Blok başlangıcında '{' eksik!"); }
        System.out.println("Çıkış <blok>");
    }

    /* Main Driver */
    public static void main(String[] args) {
        try {
            in_fp = new PushbackReader(new FileReader("front.in"));
            getChar();
            lex();
            program();
            in_fp.close();
        } catch (FileNotFoundException e) {
            System.out.println("HATA - front.in açılamadı");
        } catch (IOException e) {
            System.out.println("Okuma hatası: " + e.getMessage());
        }
    }
}