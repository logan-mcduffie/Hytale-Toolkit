package joptsimple;

abstract class OptionParserState {
   static OptionParserState noMoreOptions() {
      return new OptionParserState() {
         @Override
         protected void handleArgument(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions) {
            parser.handleNonOptionArgument(arguments.next(), arguments, detectedOptions);
         }
      };
   }

   static OptionParserState moreOptions(final boolean posixlyCorrect) {
      return new OptionParserState() {
         @Override
         protected void handleArgument(OptionParser parser, ArgumentList arguments, OptionSet detectedOptions) {
            String candidate = arguments.next();

            try {
               if (ParserRules.isOptionTerminator(candidate)) {
                  parser.noMoreOptions();
                  return;
               }

               if (ParserRules.isLongOptionToken(candidate)) {
                  parser.handleLongOptionToken(candidate, arguments, detectedOptions);
                  return;
               }

               if (ParserRules.isShortOptionToken(candidate)) {
                  parser.handleShortOptionToken(candidate, arguments, detectedOptions);
                  return;
               }
            } catch (UnrecognizedOptionException var6) {
               if (!parser.doesAllowsUnrecognizedOptions()) {
                  throw var6;
               }
            }

            if (posixlyCorrect) {
               parser.noMoreOptions();
            }

            parser.handleNonOptionArgument(candidate, arguments, detectedOptions);
         }
      };
   }

   protected abstract void handleArgument(OptionParser var1, ArgumentList var2, OptionSet var3);
}
