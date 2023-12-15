

String buildLog = new File('target/it/warning-configured-annotation-processor/usage/target/classes/more-warnings.txt').text


def matcher = buildLog =~ /you defined/

assert matcher.size() == 5


