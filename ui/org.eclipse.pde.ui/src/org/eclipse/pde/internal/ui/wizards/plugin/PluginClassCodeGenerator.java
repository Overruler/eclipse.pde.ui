 p a c k a g e   o r g . e c l i p s e . p d e . i n t e r n a l . u i . w i z a r d s . p l u g i n ;  
  
 i m p o r t   j a v a . i o . * ;  
 i m p o r t   j a v a . u t i l . * ;  
  
 i m p o r t   o r g . e c l i p s e . c o r e . r e s o u r c e s . * ;  
 i m p o r t   o r g . e c l i p s e . c o r e . r u n t i m e . * ;  
 i m p o r t   o r g . e c l i p s e . p d e . c o r e . p l u g i n . * ;  
 i m p o r t   o r g . e c l i p s e . p d e . i n t e r n a l . c o r e . * ;  
 i m p o r t   o r g . e c l i p s e . p d e . i n t e r n a l . u i . w i z a r d s . t e m p l a t e s . * ;  
 i m p o r t   o r g . e c l i p s e . p d e . u i . * ;  
  
 p u b l i c   c l a s s   P l u g i n C l a s s C o d e G e n e r a t o r   {  
 	 p r i v a t e   I P l u g i n F i e l d D a t a   f P l u g i n D a t a ;  
 	 p r i v a t e   I P r o j e c t   f P r o j e c t ;  
 	 p r i v a t e   S t r i n g   f Q u a l i f i e d C l a s s N a m e ;  
  
 	 p u b l i c   P l u g i n C l a s s C o d e G e n e r a t o r ( I P r o j e c t   p r o j e c t ,   S t r i n g   q u a l i f i e d C l a s s N a m e ,   I P l u g i n F i e l d D a t a   d a t a )   {  
 	 	 t h i s . f P r o j e c t   =   p r o j e c t ;  
 	 	 t h i s . f Q u a l i f i e d C l a s s N a m e   =   q u a l i f i e d C l a s s N a m e ;  
 	 	 f P l u g i n D a t a   =   d a t a ;  
 	 }  
 	  
 	 p u b l i c   I F i l e   g e n e r a t e ( I P r o g r e s s M o n i t o r   m o n i t o r )   t h r o w s   C o r e E x c e p t i o n   {  
 	 	 i n t   n a m e l o c   =   f Q u a l i f i e d C l a s s N a m e . l a s t I n d e x O f ( ' . ' ) ;  
 	 	 S t r i n g   p a c k a g e N a m e   =   ( n a m e l o c   = =   - 1 ) ?   " "   :   f Q u a l i f i e d C l a s s N a m e . s u b s t r i n g ( 0 ,   n a m e l o c ) ;   / / $ N O N - N L S - 1 $  
 	 	 S t r i n g   c l a s s N a m e   =   f Q u a l i f i e d C l a s s N a m e . s u b s t r i n g ( n a m e l o c   +   1 ) ;  
  
 	 	 I P a t h   p a t h   =   n e w   P a t h ( p a c k a g e N a m e . r e p l a c e ( ' . ' ,   ' / ' ) ) ;  
 	 	 i f   ( f P l u g i n D a t a . g e t S o u r c e F o l d e r N a m e ( ) . t r i m ( ) . l e n g t h ( )   >   0 )   {  
 	 	 	 p a t h   =   n e w   P a t h ( f P l u g i n D a t a . g e t S o u r c e F o l d e r N a m e ( ) ) . a p p e n d ( p a t h ) ;  
 	 	 }  
 	 	 C o r e U t i l i t y . c r e a t e F o l d e r ( f P r o j e c t . g e t F o l d e r ( p a t h ) ,   t r u e ,   t r u e ,   n u l l ) ;  
  
 	 	 I F i l e   f i l e   =   f P r o j e c t . g e t F i l e ( p a t h . a p p e n d ( c l a s s N a m e   +   " . j a v a " ) ) ;   / / $ N O N - N L S - 1 $  
 	 	 S t r i n g W r i t e r   s w r i t e r   =   n e w   S t r i n g W r i t e r ( ) ;  
 	 	 P r i n t W r i t e r   w r i t e r   =   n e w   P r i n t W r i t e r ( s w r i t e r ) ;  
 	 	 i f   ( f P l u g i n D a t a . i s L e g a c y ( ) )   {  
 	 	 	 g e n e r a t e L e g a c y P l u g i n C l a s s ( p a c k a g e N a m e ,   c l a s s N a m e ,   w r i t e r ) ;  
 	 	 }   e l s e   {  
 	 	 	 g e n e r a t e P l u g i n C l a s s ( p a c k a g e N a m e ,   c l a s s N a m e ,   w r i t e r ) ;  
 	 	 }  
 	 	 w r i t e r . f l u s h ( ) ;  
 	 	 t r y   {  
 	 	 	 s w r i t e r . c l o s e ( ) ;  
 	 	 	 B y t e A r r a y I n p u t S t r e a m   s t r e a m   =  
 	 	 	 	 n e w   B y t e A r r a y I n p u t S t r e a m ( s w r i t e r . t o S t r i n g ( ) . g e t B y t e s ( f P r o j e c t . g e t D e f a u l t C h a r s e t ( ) ) ) ;  
 	 	 	 i f   ( f i l e . e x i s t s ( ) )  
 	 	 	 	 f i l e . s e t C o n t e n t s ( s t r e a m ,   f a l s e ,   t r u e ,   m o n i t o r ) ;  
 	 	 	 e l s e  
 	 	 	 	 f i l e . c r e a t e ( s t r e a m ,   f a l s e ,   m o n i t o r ) ;  
 	 	 	 s t r e a m . c l o s e ( ) ;  
 	 	 }   c a t c h   ( I O E x c e p t i o n   e )   {  
 	 	 }  
 	 	 r e t u r n   f i l e ;  
 	 }  
  
 	  
 	 p r i v a t e   v o i d   g e n e r a t e P l u g i n C l a s s ( S t r i n g   p a c k a g e N a m e ,   S t r i n g   c l a s s N a m e ,   P r i n t W r i t e r   w r i t e r )   {  
 	 	 i f   ( ! p a c k a g e N a m e . e q u a l s ( " " ) )   {   / / $ N O N - N L S - 1 $  
 	 	 	 w r i t e r . p r i n t l n ( " p a c k a g e   "   +   p a c k a g e N a m e   +   " ; " ) ;   / / $ N O N - N L S - 1 $   / / $ N O N - N L S - 2 $  
 	 	 	 w r i t e r . p r i n t l n ( ) ;  
 	 	 }  
 	 	 i f   ( f P l u g i n D a t a . i s U I P l u g i n ( ) )  
 	 	 	 w r i t e r . p r i n t l n ( " i m p o r t   o r g . e c l i p s e . u i . p l u g i n . * ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 e l s e  
 	 	 	 w r i t e r . p r i n t l n ( " i m p o r t   o r g . e c l i p s e . c o r e . r u n t i m e . P l u g i n ; " ) ;  
 	 	 w r i t e r . p r i n t l n ( " i m p o r t   o r g . o s g i . f r a m e w o r k . B u n d l e C o n t e x t ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " i m p o r t   j a v a . u t i l . * ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( ) ;  
 	 	 w r i t e r . p r i n t l n ( " / * * " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( "   *   T h e   m a i n   p l u g i n   c l a s s   t o   b e   u s e d   i n   t h e   d e s k t o p . " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( "   * / " ) ;   / / $ N O N - N L S - 1 $  
 	 	 i f   ( f P l u g i n D a t a . i s U I P l u g i n ( ) )  
 	 	 	 w r i t e r . p r i n t l n ( " p u b l i c   c l a s s   "   +   c l a s s N a m e   +   "   e x t e n d s   A b s t r a c t U I P l u g i n   { " ) ;   / / $ N O N - N L S - 1 $   / / $ N O N - N L S - 2 $  
 	 	 e l s e  
 	 	 	 w r i t e r . p r i n t l n ( " p u b l i c   c l a s s   "   +   c l a s s N a m e   +   "   e x t e n d s   P l u g i n   { " ) ;   / / $ N O N - N L S - 1 $   / / $ N O N - N L S - 2 $  
 	 	 w r i t e r . p r i n t l n ( " \ t / / T h e   s h a r e d   i n s t a n c e . " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t p r i v a t e   s t a t i c   "   +   c l a s s N a m e   +   "   p l u g i n ; " ) ;   / / $ N O N - N L S - 1 $   / / $ N O N - N L S - 2 $  
 	 	 w r i t e r . p r i n t l n ( " \ t / / R e s o u r c e   b u n d l e . " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t p r i v a t e   R e s o u r c e B u n d l e   r e s o u r c e B u n d l e ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t / * * " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   *   T h e   c o n s t r u c t o r . " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   * / " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t p u b l i c   "   +   c l a s s N a m e   +   " ( )   { " ) ;   / / $ N O N - N L S - 1 $   / / $ N O N - N L S - 2 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t s u p e r ( ) ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t p l u g i n   =   t h i s ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t t r y   { " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t \ t r e s o u r c e B u n d l e   =   R e s o u r c e B u n d l e . g e t B u n d l e ( \ " "   / / $ N O N - N L S - 1 $  
 	 	 	 	 +   p a c k a g e N a m e   +   " . "   +   c l a s s N a m e   +   " R e s o u r c e s \ " ) ; " ) ;   / / $ N O N - N L S - 1 $   / / $ N O N - N L S - 2 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t }   c a t c h   ( M i s s i n g R e s o u r c e E x c e p t i o n   x )   { " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t \ t r e s o u r c e B u n d l e   =   n u l l ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t } " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t } " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( ) ;  
 	 	  
 	 	 w r i t e r . p r i n t l n ( " \ t / * * " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   *   T h i s   m e t h o d   i s   c a l l e d   u p o n   p l u g - i n   a c t i v a t i o n " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   * / " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t p u b l i c   v o i d   s t a r t ( B u n d l e C o n t e x t   c o n t e x t )   t h r o w s   E x c e p t i o n   { " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t s u p e r . s t a r t ( c o n t e x t ) ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t } " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( ) ;  
  
 	 	 w r i t e r . p r i n t l n ( " \ t / * * " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   *   T h i s   m e t h o d   i s   c a l l e d   w h e n   t h e   p l u g - i n   i s   s t o p p e d " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   * / " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t p u b l i c   v o i d   s t o p ( B u n d l e C o n t e x t   c o n t e x t )   t h r o w s   E x c e p t i o n   { " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t s u p e r . s t o p ( c o n t e x t ) ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t } " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( ) ;  
  
 	 	 w r i t e r . p r i n t l n ( " \ t / * * " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   *   R e t u r n s   t h e   s h a r e d   i n s t a n c e . " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   * / " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t p u b l i c   s t a t i c   "   +   c l a s s N a m e   +   "   g e t D e f a u l t ( )   { " ) ;   / / $ N O N - N L S - 1 $   / / $ N O N - N L S - 2 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t r e t u r n   p l u g i n ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t } " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( ) ;  
 	 	 w r i t e r . p r i n t l n ( " \ t / * * " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r  
 	 	 	 	 . p r i n t l n ( " \ t   *   R e t u r n s   t h e   s t r i n g   f r o m   t h e   p l u g i n ' s   r e s o u r c e   b u n d l e , " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   *   o r   ' k e y '   i f   n o t   f o u n d . " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   * / " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r  
 	 	 	 	 . p r i n t l n ( " \ t p u b l i c   s t a t i c   S t r i n g   g e t R e s o u r c e S t r i n g ( S t r i n g   k e y )   { " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t R e s o u r c e B u n d l e   b u n d l e   =   "   +   c l a s s N a m e   / / $ N O N - N L S - 1 $  
 	 	 	 	 +   " . g e t D e f a u l t ( ) . g e t R e s o u r c e B u n d l e ( ) ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t t r y   { " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r  
 	 	 	 	 . p r i n t l n ( " \ t \ t \ t r e t u r n   ( b u n d l e   ! =   n u l l )   ?   b u n d l e . g e t S t r i n g ( k e y )   :   k e y ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t }   c a t c h   ( M i s s i n g R e s o u r c e E x c e p t i o n   e )   { " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t \ t r e t u r n   k e y ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t } " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t } " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( ) ;  
 	 	 w r i t e r . p r i n t l n ( " \ t / * * " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   *   R e t u r n s   t h e   p l u g i n ' s   r e s o u r c e   b u n d l e , " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   * / " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t p u b l i c   R e s o u r c e B u n d l e   g e t R e s o u r c e B u n d l e ( )   { " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t r e t u r n   r e s o u r c e B u n d l e ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t } " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " } " ) ;   / / $ N O N - N L S - 1 $  
 	 }  
 	 p r i v a t e   v o i d   g e n e r a t e L e g a c y P l u g i n C l a s s ( S t r i n g   p a c k a g e N a m e ,   S t r i n g   c l a s s N a m e ,  
 	 	 	 P r i n t W r i t e r   w r i t e r )   {  
 	 	 i f   ( ! p a c k a g e N a m e . e q u a l s ( " " ) )   {   / / $ N O N - N L S - 1 $  
 	 	 	 w r i t e r . p r i n t l n ( " p a c k a g e   "   +   p a c k a g e N a m e   +   " ; " ) ;   / / $ N O N - N L S - 1 $   / / $ N O N - N L S - 2 $  
 	 	 	 w r i t e r . p r i n t l n ( ) ;  
 	 	 }  
 	 	 i f   ( f P l u g i n D a t a . i s U I P l u g i n ( ) )  
 	 	 	 w r i t e r . p r i n t l n ( " i m p o r t   o r g . e c l i p s e . u i . p l u g i n . * ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " i m p o r t   o r g . e c l i p s e . c o r e . r u n t i m e . * ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " i m p o r t   j a v a . u t i l . * ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( ) ;  
 	 	 w r i t e r . p r i n t l n ( " / * * " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( "   *   T h e   m a i n   p l u g i n   c l a s s   t o   b e   u s e d   i n   t h e   d e s k t o p . " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( "   * / " ) ;   / / $ N O N - N L S - 1 $  
 	 	 i f   ( f P l u g i n D a t a . i s U I P l u g i n ( ) )  
 	 	 	 w r i t e r . p r i n t l n ( " p u b l i c   c l a s s   "   +   c l a s s N a m e   +   "   e x t e n d s   A b s t r a c t U I P l u g i n   { " ) ;   / / $ N O N - N L S - 1 $   / / $ N O N - N L S - 2 $  
 	 	 e l s e  
 	 	 	 w r i t e r . p r i n t l n ( " p u b l i c   c l a s s   "   +   c l a s s N a m e   +   "   e x t e n d s   P l u g i n   { " ) ;   / / $ N O N - N L S - 1 $   / / $ N O N - N L S - 2 $  
 	 	 w r i t e r . p r i n t l n ( " \ t / / T h e   s h a r e d   i n s t a n c e . " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t p r i v a t e   s t a t i c   "   +   c l a s s N a m e   +   "   p l u g i n ; " ) ;   / / $ N O N - N L S - 1 $   / / $ N O N - N L S - 2 $  
 	 	 w r i t e r . p r i n t l n ( " \ t / / R e s o u r c e   b u n d l e . " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t p r i v a t e   R e s o u r c e B u n d l e   r e s o u r c e B u n d l e ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t / * * " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   *   T h e   c o n s t r u c t o r . " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   * / " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t p u b l i c   "   +   c l a s s N a m e   / / $ N O N - N L S - 1 $  
 	 	 	 	 +   " ( I P l u g i n D e s c r i p t o r   d e s c r i p t o r )   { " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t s u p e r ( d e s c r i p t o r ) ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t p l u g i n   =   t h i s ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t t r y   { " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t \ t r e s o u r c e B u n d l e       =   R e s o u r c e B u n d l e . g e t B u n d l e ( \ " "   / / $ N O N - N L S - 1 $  
 	 	 	 	 +   p a c k a g e N a m e   +   " . "   +   c l a s s N a m e   +   " R e s o u r c e s \ " ) ; " ) ;   / / $ N O N - N L S - 1 $   / / $ N O N - N L S - 2 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t }   c a t c h   ( M i s s i n g R e s o u r c e E x c e p t i o n   x )   { " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t \ t r e s o u r c e B u n d l e   =   n u l l ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t } " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t } " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( ) ;  
 	 	 w r i t e r . p r i n t l n ( " \ t / * * " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   *   R e t u r n s   t h e   s h a r e d   i n s t a n c e . " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   * / " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t p u b l i c   s t a t i c   "   +   c l a s s N a m e   +   "   g e t D e f a u l t ( )   { " ) ;   / / $ N O N - N L S - 1 $   / / $ N O N - N L S - 2 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t r e t u r n   p l u g i n ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t } " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( ) ;  
 	 	 w r i t e r . p r i n t l n ( " \ t / * * " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r  
 	 	 	 	 . p r i n t l n ( " \ t   *   R e t u r n s   t h e   s t r i n g   f r o m   t h e   p l u g i n ' s   r e s o u r c e   b u n d l e , " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   *   o r   ' k e y '   i f   n o t   f o u n d . " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   * / " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r  
 	 	 	 	 . p r i n t l n ( " \ t p u b l i c   s t a t i c   S t r i n g   g e t R e s o u r c e S t r i n g ( S t r i n g   k e y )   { " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t R e s o u r c e B u n d l e   b u n d l e   =   "   +   c l a s s N a m e   / / $ N O N - N L S - 1 $  
 	 	 	 	 +   " . g e t D e f a u l t ( ) . g e t R e s o u r c e B u n d l e ( ) ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t t r y   { " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r  
 	 	 	 	 . p r i n t l n ( " \ t \ t \ t r e t u r n   ( b u n d l e   ! =   n u l l )   ?   b u n d l e . g e t S t r i n g ( k e y )   :   k e y ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t }   c a t c h   ( M i s s i n g R e s o u r c e E x c e p t i o n   e )   { " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t \ t r e t u r n   k e y ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t } " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t } " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( ) ;  
 	 	 w r i t e r . p r i n t l n ( " \ t / * * " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   *   R e t u r n s   t h e   p l u g i n ' s   r e s o u r c e   b u n d l e , " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t   * / " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t p u b l i c   R e s o u r c e B u n d l e   g e t R e s o u r c e B u n d l e ( )   { " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t \ t r e t u r n   r e s o u r c e B u n d l e ; " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " \ t } " ) ;   / / $ N O N - N L S - 1 $  
 	 	 w r i t e r . p r i n t l n ( " } " ) ;   / / $ N O N - N L S - 1 $  
 	 }  
 	  
 	 p u b l i c   I P l u g i n R e f e r e n c e [ ]   g e t D e p e n d e n c i e s ( )   {  
 	 	 A r r a y L i s t   r e s u l t   =   n e w   A r r a y L i s t ( ) ;  
 	 	 i f   ( f P l u g i n D a t a . i s U I P l u g i n ( ) )  
 	 	 	 r e s u l t . a d d ( n e w   P l u g i n R e f e r e n c e ( " o r g . e c l i p s e . u i " ,   n u l l ,   0 ) ) ;   / / $ N O N - N L S - 1 $  
 	 	 i f   ( ! f P l u g i n D a t a . i s L e g a c y ( ) )  
 	 	 	 r e s u l t . a d d ( n e w   P l u g i n R e f e r e n c e ( " o r g . e c l i p s e . c o r e . r u n t i m e " ,   n u l l ,   0 ) ) ;   / / $ N O N - N L S - 1 $  
 	 	 r e t u r n   ( I P l u g i n R e f e r e n c e [ ] )   r e s u l t . t o A r r a y ( n e w   I P l u g i n R e f e r e n c e [ r e s u l t . s i z e ( ) ] ) ;  
 	 }  
  
 }  
