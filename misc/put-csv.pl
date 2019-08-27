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
use File::Slurp;

$Getopt::Std::STANDARD_HELP_VERSION = 1;
our $VERSION = "0.1";

sub VERSION_MESSAGE {
    say STDERR $VERSION;
}

sub HELP_MESSAGE {
    say STDERR "Usage: perl $0 -t\$TOKEN -f\$CSV_FILE";
}

# main
{
    my %opts = (
        e => 'http://localhost:8080',
    );
    getopts('e:t:f:', \%opts);
    ### opts : %opts
    croak 'No token supplied' unless exists $opts{t};
    croak 'No csv filename supplied' unless exists $opts{f};

    my $filename = $opts{f};
    croak "File not found: ${filename}" unless -e $filename;

    my $url = $opts{e} . '/history';
    my $req = HTTP::Request->new(PUT => $url);
    $req->header('Authorization', 'Bearer ' . $opts{t});

    my $text = read_file($filename);
    $req->content_type('text/plain;charset=UTF-8');
    $req->content($text);

    my $ua = LWP::UserAgent->new;
    my $res = $ua->request($req);
    ### response : $res
    croak "Fail with HTTP Status: " . $res->status_line . " (" . $res->content . ")" unless $res->is_success;

    say $res->content;
}