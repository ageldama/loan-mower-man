use utf8;
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
    say STDERR "Usage: perl $0 -t";
}

# main
{
    my %opts = (
        e => 'http://localhost:8080',
    );
    getopts('e:t:', \%opts);
    ### opts : %opts
    croak 'No token supplied' unless exists $opts{t};

    my $url = $opts{e} . '/predictions/';
    my $req = HTTP::Request->new(POST => $url);
    $req->header('Authorization', 'Bearer ' . $opts{t});

    my %req_payload = (
        bank => '신한은행',
        month => 2,
    );
    my $req_json = encode_json(\%req_payload);
    ### req_json : $req_json
    $req->content_type('application/json;charset=UTF-8');
    $req->content($req_json);

    my $ua = LWP::UserAgent->new;
    my $res = $ua->request($req);
    ### response : $res
    croak "Fail with HTTP Status: " . $res->status_line . " (" . $res->content . ")" unless $res->is_success;

    say $res->content;
}
