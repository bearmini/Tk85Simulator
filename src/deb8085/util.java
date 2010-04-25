package   deb8085;


//***************************************************************************************************
//***************************************************************************************************
// ���[�e�B���e�B�֐��Q
public class   util
  {

  // �Q�o�C�g�����̃G���f�B�A�����t�ɂ���
  public static int   swapEndian( int   x )
    {
    return ( (x&0xFF00) >>> 8 ) + ( (x&0xFF) << 8 );
    }

  // �n���ꂽ�������\�Z�i���S���\���ɂ���
  public static String   hex4( int   x )
    {
    int   x0, x1, x2, x3;

    x0 = x&0x000F;
    x1 = (x&0x00F0) >>> 4;
    x2 = (x&0x0F00) >>> 8;
    x3 = (x&0xF000) >>> 12;

    return ""+ Character.forDigit( x3, 16 )+ Character.forDigit( x2, 16 )+ Character.forDigit( x1, 16 )+ Character.forDigit( x0, 16 );
    }

  // �n���ꂽ�������\�Z�i���Q���\���ɂ���
  public static String   hex2( int   x )
    {
    int   x0, x1;

    x0 = x&0x000F;
    x1 = (x&0x00F0) >>> 4;

    return ""+ Character.forDigit( x1, 16 )+ Character.forDigit( x0, 16 );
    }


  // �\�Z�i���t�ϊ�
  public static int   unhex( String   hex )
    {
    if( hex == null )
      return   0;

    int   result = 0;

    hex.trim();
    while( !hex.equals( "" ) )
      {
      char   c = hex.charAt( 0 );
      if( c == ' ' )
        result <<= 4;
      else
        result = ( result << 4 ) + Character.digit( c, 16 );

      hex = hex.substring( 1 );
      }

    return result&0xFFFF;
    }


  // �f�t�H���g�l�t���̏\�Z�i���t�ϊ�
/*
  public static int   unhex( String   hex, int   default )
    {
    hex.trim();

    if( hex.equals("") )
      return default;
    else
      return unhex(hex);
    }

*/


  // �w�肳�ꂽ���̃X�y�[�X��Ԃ�
  public static String   space( int   n )
    {
    String   result = "";

    for( int   i=0; i<n; i++ )
      result += " ";

    return   result;
    }



  // �w�肳�ꂽ�A�X�L�[�R�[�h���������𕶎���ɂ��ĕԂ�
  public static char   makeValidChar( char   a )
    {
    if( ' '<=a && a<='~' )
      return   a;
    else
      return   '.';
    }



  // �r�b�g�������Ă��邩�ǂ����`�F�b�N����
  public static boolean   bitOn( int   x, int   bit )
    {
    return ( ( x & (1<<bit) )!= 0);
    }


  // �r�b�g�����Ă�
  public static int   setBit( int   x, int   bit, boolean   set )
    {
    if( set )
      return  ( x | (1<<bit) );
    else
      return  ( x & ((1<<bit)^0xff ) );
    }


  }