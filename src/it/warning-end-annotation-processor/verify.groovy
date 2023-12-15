

String buildLog = new File('target/it/warning-end-annotation-processor/usage/target/classes/warnings.txt').text


def matcher = buildLog =~ /you defined/

assert matcher.size() == 5


