use strict;
use warnings;
use feature qw(say);
use Carp;
use Data::Dumper;
use Getopt::Std;
use Smart::Comments;
use JSON;
use HTTP::Request;
use LWP::UserAgent;

$Getopt::Std::STANDARD_HELP_VERSION = 1;
our $VERSION = "0.1";

sub VERSION_MESSAGE {
    say STDERR $VERSION;
}

sub HELP_MESSAGE {
    say STDERR "Usage: perl $0 -u\$USERNAME -p\$PASSWORD";
}

# main
{
    my %opts = (
        e => 'http://localhost:8080',
    );
    getopts('e:u:p:', \%opts);
    ### opts : %opts
    croak 'No username supplied' unless exists $opts{u};
    croak 'No password supplied' unless exists $opts{p};

    #
    my %req_payload = (
        id => $opts{u},
        password => $opts{p},
    );
    my $req_json = encode_json(\%req_payload);
    ### req_json : $req_json

    my $url = $opts{e} . '/api-user/signup';
    my $req = HTTP::Request->new(POST => $url);
    $req->content_type('application/json;charset=UTF-8');
    $req->content($req_json);

    my $ua = LWP::UserAgent->new; # You might want some options here
    my $res = $ua->request($req);
    ### response : $res
    croak "Fail with HTTP Status: " . $res->status_line . " (" . $res->content . ")" unless $res->is_success;

    say $res->header('Token');
}
