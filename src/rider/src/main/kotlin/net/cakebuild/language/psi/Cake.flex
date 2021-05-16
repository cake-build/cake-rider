package net.cakebuild.language.psi;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import net.cakebuild.language.psi.CakeTypes;

%%

%class CakeLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF = \r|\n|\r\n
INPUT_CHAR = [^\r\n]
WHITE_SPACE = {CRLF} | [ \t\f]

/* comments */
BLOCK_COMMENT_START = "/*"
BLOCK_COMMENT_END = "*/"
// Comment can be the last line of the file, without line terminator.
EOL_COMMENT     = "/" "/"+  {INPUT_CHAR}*
TASK_START = "Task(\""
TASK_END = "\")"
TASK_NAME = [^\"\r\n]+

%state TASK
%state BLOCK_COMMENT

%%

<YYINITIAL> {BLOCK_COMMENT_START}                           { yybegin(BLOCK_COMMENT); return CakeTypes.BLOCK_COMMENT; }

<YYINITIAL> {TASK_START}                                    { yybegin(TASK); return CakeTypes.TASK_START; }

<YYINITIAL> {CRLF}({CRLF}|{WHITE_SPACE})+                   { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<YYINITIAL> {EOL_COMMENT}                                   { yybegin(YYINITIAL); return CakeTypes.EOL_COMMENT; }

<TASK> {WHITE_SPACE}+                                       { yybegin(TASK); return TokenType.WHITE_SPACE; }

<TASK> {TASK_NAME}                                          { yybegin(TASK); return CakeTypes.TASK_NAME; }

<TASK> {TASK_END}                                           { yybegin(YYINITIAL); return CakeTypes.TASK_END; }

<BLOCK_COMMENT> {INPUT_CHAR}+                               { yybegin(BLOCK_COMMENT); return CakeTypes.BLOCK_COMMENT; }

<BLOCK_COMMENT> {WHITE_SPACE}+                              { yybegin(BLOCK_COMMENT); return CakeTypes.BLOCK_COMMENT; }

<BLOCK_COMMENT> {BLOCK_COMMENT_END}                         { yybegin(YYINITIAL); return CakeTypes.BLOCK_COMMENT; }

({CRLF}|{WHITE_SPACE})+                                     { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

[^]                                                         { return CakeTypes.UNKNOWN; }
