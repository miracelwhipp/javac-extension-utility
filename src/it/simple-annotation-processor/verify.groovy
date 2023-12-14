

String buildLog = new File('target/it/simple-annotation-processor/usage/target/classes/pack/build.log').text


def matcher = buildLog =~ /adding sum/

assert matcher.size() == 5


matcher = buildLog =~ /finished. sum is 10/

assert matcher.size() == 1

