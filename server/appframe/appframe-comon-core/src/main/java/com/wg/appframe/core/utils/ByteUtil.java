package com.wg.appframe.core.utils;

import javax.crypto.IllegalBlockSizeException;

/**
 * @Author: yang_jh
 * @Description:
 * @Date: Created in 2018/7/17
 */
public class ByteUtil {
    /**
     * Construtor da classe, definido como private para impedir que ela seja
     * instanciada.
     */
    private ByteUtil() {
    }
    
    /**
     * Converts byte to binary representation.
     *
     * @param in byte that will be converted.
     * @return String with hex representation of in.
     */
    public static String toBin(int in) {
        
        return "0b" + toBinGeneric(in);
    }
    
    /**
     * Converts byte[] to binary representation.
     *
     * @param in byte[] that will be converted.
     * @return String with hex representation of in.
     */
    public static String toBin(byte[] in) {
        
        StringBuffer tmp = new StringBuffer();
        
        for (int i = 0; i < in.length; i++) {
            tmp.append(toBin(in[i]) + " ");
        }
        
        return "0b " + tmp.toString();
    }
    
    /**
     * Converts byte to binary representation.
     *
     * @param in byte that will be converted.
     * @return String with hex representation of in.
     */
    private static String toBinGeneric(int in) {
        
        StringBuffer tmp = new StringBuffer();
        
        for (int i = 0; i < 8; i++) {
            tmp.insert(0, ((in >> i) & 0x01));
        }
        
        return tmp.toString();
    }
    
    /**
     * Converts byte to hex representation.
     *
     * @param in byte that will be converted.
     * @return String with hex representation of in.
     */
    public static String toHex(byte in) {
        
        return "0x" + toHexGeneric(in);
    }
    
    /**
     * Converts byte[] to hex representation.
     *
     * @param in byte[] that will be converted.
     * @return String with hex representation of in.
     */
    public static String toHex(byte[] in) {
        
        StringBuffer tmp = new StringBuffer("0x");
        
        for (int i = 0; i < in.length; i++) {
            tmp.append(toHexGeneric(in[i]));
        }
        
        return tmp.toString();
    }
    
    /**
     * Converts byte to binary representation.
     *
     * @param in byte that will be converted.
     * @return String with hex representation of in.
     */
    private static String toHexGeneric(byte in) {
        
        String tmp = "";
        
        byte b = (byte) (in & 0x000000f0);
        
        switch (b) {
            
            case 0x00:
                tmp += "0";
                break;
            
            case 0x10:
                tmp += "1";
                break;
            
            case 0x20:
                tmp += "2";
                break;
            
            case 0x30:
                tmp += "3";
                break;
            
            case 0x40:
                tmp += "4";
                break;
            
            case 0x50:
                tmp += "5";
                break;
            
            case 0x60:
                tmp += "6";
                break;
            
            case 0x70:
                tmp += "7";
                break;
            
            case (byte) 0x80:
                tmp += "8";
                break;
            
            case (byte) 0x90:
                tmp += "9";
                break;
            
            case (byte) 0xa0:
                tmp += "a";
                break;
            
            case (byte) 0xb0:
                tmp += "b";
                break;
            
            case (byte) 0xc0:
                tmp += "c";
                break;
            
            case (byte) 0xd0:
                tmp += "d";
                break;
            
            case (byte) 0xe0:
                tmp += "e";
                break;
            
            case (byte) 0xf0:
                tmp += "f";
                break;
            
        }
        
        b = (byte) (in & 0x0000000f);
        
        switch (b) {
            
            case 0x00:
                tmp += "0";
                break;
            
            case 0x01:
                tmp += "1";
                break;
            
            case 0x02:
                tmp += "2";
                break;
            
            case 0x03:
                tmp += "3";
                break;
            
            case 0x04:
                tmp += "4";
                break;
            
            case 0x05:
                tmp += "5";
                break;
            
            case 0x06:
                tmp += "6";
                break;
            
            case 0x07:
                tmp += "7";
                break;
            
            case 0x08:
                tmp += "8";
                break;
            
            case 0x09:
                tmp += "9";
                break;
            
            case 0x0a:
                tmp += "a";
                break;
            
            case 0x0b:
                tmp += "b";
                break;
            
            case 0x0c:
                tmp += "c";
                break;
            
            case 0x0d:
                tmp += "d";
                break;
            
            case 0x0e:
                tmp += "e";
                break;
            
            case 0x0f:
                tmp += "f";
                break;
            
        }
        
        return tmp;
    }
    
    /**
     * Converts byte[8] to long representation.
     *
     * @param input       byte[8] that will be converted.
     * @param inputOffset the offset in input where input starts.
     * @return the long representation.
     * @throws IllegalBlockSizeException if input length lesser than 8.
     */
    public static long toLong(byte[] input, int inputOffset)
            throws IllegalBlockSizeException {
        if (input.length - inputOffset < 8) {
            throw new IllegalBlockSizeException("Usable byte range is "
                    + (input.length - inputOffset)
                    + " bytes large, but it should be 8 bytes or larger.");
        }
        
        long returnValue = 0L;
        
        for (int i = inputOffset; i - inputOffset < 8; i++) {
            returnValue |= (input[i] & 0x00000000000000ffL) << (64 - 8 - 8 * (i - inputOffset));
        }
        
        return returnValue;
        
    }
    
    /**
     * Converte um array de (4) bytes em um int.
     *
     * @param bytes o array de bytes a ser convertido
     * @return o int formado pelos bytes dados.
     */
    public static int byteArrayToInt(byte[] bytes) {
        int result = 0;
        result = result | (0xFF000000 & (bytes[0] << 24));
        result = result | (0x00FF0000 & (bytes[1] << 16));
        result = result | (0x0000FF00 & (bytes[2] << 8));
        result = result | (0x000000FF & (bytes[3]));
        return result;
    }
    
    /**
     * Converte um array de (8) bytes em um long.
     *
     * @param bytes o array de bytes a ser convertido
     * @return o long formado pelos bytes dados.
     */
    public static long byteArrayToLong(byte[] bytes) {
        long result = 0;
        result = result | (0xFF00000000000000L & (((long) bytes[0]) << 56));
        result = result | (0x00FF000000000000L & (((long) bytes[1]) << 48));
        result = result | (0x0000FF0000000000L & (((long) bytes[2]) << 40));
        result = result | (0x000000FF00000000L & (((long) bytes[3]) << 32));
        result = result | (0x00000000FF000000L & (((long) bytes[4]) << 24));
        result = result | (0x0000000000FF0000L & (((long) bytes[5]) << 16));
        result = result | (0x000000000000FF00L & (((long) bytes[6]) << 8));
        result = result | (0x00000000000000FFL & (((long) bytes[7])));
        return result;
    }
    
    /**
     * Converte um array de (2) bytes em um short.
     *
     * @param bytes o array de bytes a ser convertido
     * @return o short formado pelos bytes dados.
     */
    public static short byteArrayToShort(byte[] bytes) {
        int result = 0;
        result = result | (0x0000FF00 & (bytes[0] << 8));
        result = result | (0x000000FF & (bytes[1]));
        return ((short) result);
    }
    
    /**
     * Converte um inteiro em um array de (4) bytes.
     *
     * @param valor o inteiro a ser convertido
     * @return o array dos bytes do inteiro dado.
     */
    public static byte[] intToByteArray(int valor) {
        byte[] result = new byte[4];
        for (int i = 0; i < result.length; i++) {
            result[3 - i] = (byte) (valor & 0xFF);
            valor = valor >> 8;
        }
        return result;
    }
    
    /**
     * Converte um long em um array de (8) bytes.
     *
     * @param valor o long a ser convertido
     * @return o array dos bytes do long dado.
     */
    public static byte[] longToByteArray(long valor) {
        byte[] result = new byte[8];
        for (int i = 0; i < result.length; i++) {
            result[7 - i] = (byte) (valor & 0xFF);
            valor = valor >> 8;
        }
        return result;
    }
    
    /**
     * Converte um short em um array de (2) bytes.
     *
     * @param valor o short a ser convertido
     * @return o array dos bytes do short dado.
     */
    public static byte[] shortToByteArray(int valor) {
        byte[] result = new byte[2];
        for (int i = 0; i < result.length; i++) {
            result[1 - i] = (byte) (valor & 0xFF);
            valor = valor >> 8;
        }
        return result;
    }
    
    /**
     * Converte uma String de volta em um array de bytes, convertida pelo mtodo
     * uuencode(byte[]).
     *
     * @param b o string a ser convertido
     * @return o array de bytes original.
     * @see #uuencode
     */
    public static byte[] uudecode(String b) {
        int i = 0;
        StringBuffer sb = new StringBuffer();
        while (b.charAt(i) != ';') {
            sb.append(b.charAt(i));
            i++;
        }
        i++;
        int tam = Integer.parseInt(sb.toString());
        byte[] result = new byte[tam];
        int[] in = new int[4];
        byte[] out = new byte[3];
        int[] aux = new int[3];
        int j = 0;
        for (; i < b.length(); i += 4) {
            in[0] = (int) (b.charAt(i) - 50);
            in[1] = (int) (b.charAt(i + 1) - 50);
            in[2] = (int) (b.charAt(i + 2) - 50);
            in[3] = (int) (b.charAt(i + 3) - 50);
            aux[0] = (in[0] << 2) | ((in[1] & 0x30) >> 4);
            aux[1] = ((in[1] & 0x0F) << 4) | ((in[2] & 0x3C) >> 2);
            aux[2] = ((in[2] & 0x03) << 6) | in[3];
            for (int k = 0; k < 3; k++) {
                if (j < tam) {
                    result[j++] = (byte) aux[k];
                }
            }
        }
        return result;
    }
    
    /**
     * Converte um array de bytes em uma String formada apenas por caracteres
     * que possam ser impressos (entre 0x32 e 0x72), de acordo com um protocolo
     * proprietrio. O array pode ser obtido de volta com o mtodo
     * uudecode(String).
     *
     * @param b o array de bytes a ser convertido
     * @return uma string representando o array dado.
     * @see #uudecode
     */
    public static String uuencode(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 4 / 3);
        sb.append(b.length);
        sb.append(';');
        byte[] in = new byte[3];
        char[] out = new char[4];
        int[] aux = new int[4];
        for (int i = 0; i < b.length; i += 3) {
            in[0] = b[i];
            if ((i + 1) < b.length) {
                in[1] = b[i + 1];
            } else {
                in[1] = (byte) 0x20;
            }
            if ((i + 2) < b.length) {
                in[2] = b[i + 2];
            } else {
                in[2] = (byte) 0x20;
            }
            aux[0] = (in[0] & 0xFC) >> 2;
            aux[1] = ((in[0] & 0x03) << 4) | ((in[1] & 0xF0) >> 4);
            aux[2] = ((in[1] & 0x0F) << 2) | ((in[2] & 0xC0) >> 6);
            aux[3] = (in[2] & 0x3F);
            for (int j = 0; j < 4; j++) {
                out[j] = (char) (50 + aux[j]);
            }
            sb.append(out);
        }
        return sb.toString();
    }
    
    /**
     * Identifica o estado de um determinado bit em um mapa de bits.
     *
     * @param bitMap o mapa de bits (cada byte corresponde a 8 bits).
     * @param index  o ndice do bit a ser definido. O bit de ndice zero o mais
     *               esquerda no bitmap, ou seja, o bit mais significativo de
     *               bitMap[0]. O bit de ndice (bitMap.lenght() * 8 -1) o bit mais
     *               direita do bitmap, ou seja, o bit menos significativo de
     *               bitMap[bitMap.lenght()].
     * @return o estado do bit: true se o bit estiver setado, false caso
     * contrrio.
     * @throws ArrayIndexOutOfBoundsException caso o ndice esteja fora da faixa.
     */
    public static boolean getBit(byte[] bitMap, int index)
            throws ArrayIndexOutOfBoundsException {
        return (bitMap[index / 8] & (1 << (7 - (index % 8)))) != 0;
    }
    
    /**
     * Define o estado de um determinado bit em um mapa de bits.
     *
     * @param bitMap o mapa de bits (cada byte corresponde a 8 bits).
     * @param index  o ndice do bit a ser definido. O bit de ndice zero o mais
     *               esquerda no bitmap, ou seja, o bit mais significativo de
     *               bitMap[0]. O bit de ndice (bitMap.lenght() * 8 -1) o bit mais
     *               direita do bitmap, ou seja, o bit menos significativo de
     *               bitMap[bitMap.lenght()].
     * @param value  o valor a ser definido para o bit, se true, o bit ser setado,
     *               se false, o bit ser resetado.
     * @throws ArrayIndexOutOfBoundsException caso o ndice esteja fora da faixa.
     */
    public static void setBit(byte[] bitMap, int index, boolean value)
            throws ArrayIndexOutOfBoundsException {
        int i = index / 8;
        int mask = 1 << (7 - (index % 8));
        
        if (value) { // setar o bit
            bitMap[i] |= mask;
        } else { // resetar o bit
            bitMap[i] &= ~mask;
        }
    }
    
    /**
     * Retorna o valor decimal de um dgito hexadecimal.
     *
     * @param c o dgito hexadecimal ('0'-'9', 'a'-'f' ou 'A'-'F').
     * @return o valor do dgito hexadecimal.
     * @throws Exception ocorrer caso o caractere passado como parmetro no seja um
     *                   dgito hexadecimal vlido.
     */
    private static int hexDigitValue(char c) throws Exception {
        int retorno = 0;
        if (c >= '0' && c <= '9') {
            retorno = (int) (((byte) c) - 48);
        } else if (c >= 'A' && c <= 'F') {
            retorno = (int) (((byte) c) - 55);
        } else if (c >= 'a' && c <= 'f') {
            retorno = (int) (((byte) c) - 87);
        } else {
            throw new Exception();
        }
        return retorno;
    }
    
    /**
     * Converte uma string preenchida com dgitos hexadecimais em um array de
     * bytes. Cada byte do array corresponder a dois dgitos da String, desta
     * forma, a String dever conter um nmero par de dgitos. Exemplo: a String
     * "FFA3" ser convertida no array de bytes { 0xFF, 0xA3 }.
     *
     * @param hexa a string com os dgitos hexadecimais.
     * @return O array de bytes correspondente aos dgitos na string.
     * @throws Exception ocorrer caso a string seja nula, possua um nmero mpar de
     *                   dgitos ou caso algum dos caracteres no corresponda a um
     *                   dgito hexadecimal vlido.
     */
    
    public static byte hexToByte(String hexa) throws Exception {
        if (hexa == null) {
            throw new Exception();
        }
        if (hexa.length() != 2) {
            throw new Exception();
        }
        byte[] b = hexa.getBytes();
        byte valor = (byte) (hexDigitValue((char) b[0]) * 16 + hexDigitValue((char) b[1]));
        return valor;
    }
    
    /**
     * Converte uma string preenchida com dgitos hexadecimais em um array de
     * bytes. Cada byte do array corresponder a dois dgitos da String, desta
     * forma, a String dever conter um nmero par de dgitos. Exemplo: a String
     * "FFA3" ser convertida no array de bytes { 0xFF, 0xA3 }.
     *
     * @param hexa a string com os dgitos hexadecimais.
     * @return O array de bytes correspondente aos dgitos na string.
     * @throws Exception ocorrer caso a string seja nula, possua um nmero mpar de
     *                   dgitos ou caso algum dos caracteres no corresponda a um
     *                   dgito hexadecimal vlido.
     */
    
    public static byte[] hexToByteArray(String hexa) throws Exception {
        if (hexa == null) {
            throw new Exception();
        }
        if ((hexa.length() % 2) != 0) {
            throw new Exception();
        }
        int tamArray = hexa.length() / 2;
        byte[] retorno = new byte[tamArray];
        for (int i = 0; i < tamArray; i++) {
            retorno[i] = hexToByte(hexa.substring(i * 2, i * 2 + 2));
        }
        return retorno;
    }
    
    /**
     * Converte um array de bytes em uma string com a representacao hexadecimal
     * dos bytes contidos no array. Cada byte do array corresponder a dois
     * dgitos hexadecimais na string. Por exemplo, o array de bytes { 3, 0x40,
     * 13, -1 } seria convertido na string "03400DFF".
     *
     * @param bytes o array de bytes a ser convertido em string hexadecimal. Caso
     *              este parmetro seja nulo ou seu tamanho seja zero, a string
     *              retornada ser "".
     * @return A string com dgitos hexadecimais representando o array de bytes.
     */
    public static String byteArrayToHex(byte[] bytes) {
        String retorno = "";
        if (bytes == null || bytes.length == 0) {
            return retorno;
        }
        for (int i = 0; i < bytes.length; i++) {
            byte valor = bytes[i];
            int d1 = valor & 0xF;
            d1 += (d1 < 10) ? 48 : 55;
            int d2 = (valor & 0xF0) >> 4;
            d2 += (d2 < 10) ? 48 : 55;
            retorno = retorno + (char) d2 + (char) d1;
        }
        return retorno;
    }
    
    /**
     * Converte um byte em sua representao hexadecimal de 2 dgitos.
     *
     * @param valor byte a ser convertido
     * @return a string com o valor convertido em hexadecimal.
     */
    public static String byteToHex(byte valor) {
        int d1 = valor & 0xF;
        d1 += (d1 < 10) ? 48 : 55;
        int d2 = (valor & 0xF0) >> 4;
        d2 += (d2 < 10) ? 48 : 55;
        String ret = "" + (char) d2 + (char) d1;
        return ret;
    }
}

